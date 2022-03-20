/*
 * dev.kobalt.holdem
 * Copyright (C) 2022 Tom.K
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.kobalt.holdem.jvm.server

import kotlinx.serialization.json.*

class HoldemClient(
    val session: HoldemSession,
    val server: HoldemServer
) {
    val uid = server.generateClientUid()
    val isOwnerOfCurrentRoom get() = currentRoom?.owner?.uid == uid
    val player: HoldemPlayer? get() = currentTable?.players?.list?.find { it.client.uid == uid }
    val currentRoom: HoldemRoom? get() = server.rooms.find { it.uid == currentRoomUid }
    val currentTable: HoldemTable? get() = currentRoom?.table
    var currentRoomUid: String? = null
    var name: String = ""

    suspend fun onAction(command: String, parameter: List<String>) {
        when (command) {
            "name" -> updateName(parameter.joinToString(" "))
            "create" -> createRoom(parameter.getOrNull(0)?.toIntOrNull() ?: 10)
            "join" -> joinRoom(parameter.getOrNull(0).orEmpty())
            "leave" -> when (parameter.getOrNull(0)) {
                "room" -> leaveRoom()
                "table" -> leaveTable()
            }
            "start" -> start()
            "fold" -> player?.fold()
            "check" -> player?.check()
            "call" -> player?.call()
            "bet" -> player?.bet(parameter.getOrNull(0)?.toIntOrNull())
            "raise" -> player?.raise(parameter.getOrNull(0)?.toIntOrNull())
            "allin" -> player?.allIn()
            "ping" -> session.send("pong")
        }
    }

    suspend fun updateName(newName: String) {
        when {
            newName.isEmpty() -> sendErrorMessage("Name cannot be blank.")
            newName.length > 100 -> sendErrorMessage("Maximum length for name is 100 characters.")
            else -> {
                val oldName = name
                name = newName
                (currentRoom?.clients ?: listOf(this)).forEach {
                    it.reload()
                    it.sendLogMessage("$uid - $oldName changed name to $newName")
                }
            }
        }
    }

    suspend fun createRoom(clientLimit: Int) {
        when {
            clientLimit > 20 -> sendErrorMessage("Maximum limit is 20 players.")
            clientLimit < 2 -> sendErrorMessage("Minimum limit is 2 players.")
            else -> {
                currentRoom?.let { leaveRoom() }
                val newRoom = HoldemRoom(
                    server,
                    uid,
                    clientLimit,
                    null
                )
                server.rooms += newRoom
                joinRoom(newRoom.uid)
                newRoom.clients.forEach {
                    it.reload()
                    it.sendLogMessage("$uid - Created room ${newRoom.uid}")
                }
            }
        }
    }

    suspend fun joinRoom(roomUid: String) {
        server.rooms.find { it.uid == roomUid }?.let { newRoom ->
            when (newRoom.status) {
                HoldemStatus.Open -> {
                    currentRoom?.let { leaveRoom() }
                    newRoom.clients += this
                    currentRoomUid = newRoom.uid
                    newRoom.clients.forEach {
                        it.reload()
                        it.sendLogMessage("$uid - Joined room ${newRoom.uid}")
                    }
                }
                else -> sendErrorMessage("This room is full.")
            }
        } ?: sendErrorMessage("Room does not exist.")
    }

    suspend fun leaveRoom() {
        currentRoom?.let { room ->
            room.table?.let { table ->
                table.players.list.remove(player)
                if (table.players.size <= 1) {
                    table.players.list.clear()
                    room.table = null
                }
            }
            room.clients.remove(this)
            room.clients.find { it.uid != uid }?.also {
                room.ownerUid = it.uid
            } ?: run {
                server.rooms.remove(room)
            }
            currentRoomUid = null
            room.clients.plus(this).forEach {
                it.reload()
                it.sendLogMessage("$uid - Left room ${room.uid}")
            }
        }
    }

    suspend fun start() {
        currentRoom?.let {
            when {
                !isOwnerOfCurrentRoom -> sendErrorMessage("You are not the owner of this room.")
                it.clients.size <= 1 -> sendErrorMessage("There are not enough players in this room.")
                else -> it.start()
            }
        } ?: sendErrorMessage("You are not in a room.")
    }

    suspend fun send(value: String) = session.send(value)

    suspend fun sendErrorMessage(value: String) = send(JsonObject(mapOf("message" to JsonPrimitive(value))).toString())

    suspend fun sendLogMessage(value: String) = send(JsonObject(mapOf("log" to JsonPrimitive(value))).toString())

    suspend fun leaveTable() {
        val currentRoom = currentRoom
        val currentTable = currentTable
        val player = player
        when {
            currentRoom == null -> sendErrorMessage("You're not in a room.")
            currentTable == null -> sendErrorMessage("There is no table set up.")
            !currentTable.players.list.contains(player) || player == null -> sendErrorMessage("You're not on a table.")
            else -> {
                if (currentTable.players.current == player) {
                    currentTable.players.current = currentTable.players.playerAfter(player)
                }
                if (currentTable.players.dealer == player) {
                    currentTable.players.dealer = currentTable.players.playerAfter(player)
                }
                currentTable.players.list.remove(player)
                currentTable.pots.forEach {
                    it.eligiblePlayers.remove(player)
                }
                if (currentTable.players.size <= 1) {
                    currentTable.players.list.clear()
                    currentRoom.table = null
                }
                currentRoom.clients.forEach { it.reload() }
            }
        }
    }

    fun String.toJson() = JsonPrimitive(this)
    fun Number.toJson() = JsonPrimitive(this)

    fun List<JsonElement>.toJson() = JsonArray(this)
    fun Map<String, JsonElement>.toJson() = JsonObject(this)
    fun <T> List<T>.mapToJson(transform: (T) -> JsonElement) = map(transform).toJson()
    fun <T> Set<T>.mapToJson(transform: (T) -> JsonElement) = map(transform).toJson()
    fun mapOfJson(vararg pairs: Pair<String, JsonElement>): JsonObject = mapOf(*pairs).toJson()

    suspend fun reload() {
        send(
            mapOfJson(
                "player" to (mapOfJson(
                    "uid" to uid.toJson(),
                    "name" to name.toJson()
                ).takeIf { name.isNotEmpty() } ?: JsonNull),
                "currentRoom" to (currentRoom?.let { room ->
                    mapOfJson(
                        "uid" to room.uid.toJson(),
                        "status" to room.status.name.toJson(),
                        "playerLimit" to (room.clientsLimit.toJson()),
                        "players" to (room.clients.mapToJson { roomPlayer ->
                            mapOfJson(
                                "name" to roomPlayer.name.toJson(),
                                "uid" to roomPlayer.uid.toJson()
                            )
                        }),
                        "actions" to listOfNotNull(
                            "Leave",
                            "Start".takeIf {
                                isOwnerOfCurrentRoom && ((currentRoom?.clients?.size ?: 0) > 1) && currentTable == null
                            }
                        ).mapToJson { it.toJson() }
                    )
                } ?: JsonNull),
                "currentTable" to (currentTable?.let { table ->
                    mapOfJson(
                        "highestBet" to table.highestBet.toJson(),
                        "phase" to table.phase.name.toJson(),
                        "pots" to table.pots.mapToJson { pot ->
                            mapOfJson(
                                "amount" to pot.amount.toJson(),
                                "eligible" to pot.eligiblePlayers.joinToString { it.client.name }.toJson(),
                                "winning" to pot.winningPlayers.joinToString { it.client.name }.toJson(),
                            )
                        },
                        "hand" to table.hand.mapToJson {
                            mapOfJson("src" to it.name.toJson())
                        },
                        "currentPlayer" to (table.players.current?.let { roomPlayer ->
                            mapOfJson(
                                "name" to roomPlayer.client.name.toJson(),
                                "uid" to roomPlayer.client.uid.toJson(),
                                "hand" to (if (roomPlayer.hand.isEmpty()) emptyList() else roomPlayer.hand.takeIf { roomPlayer.client.uid == uid }
                                    ?: listOf(
                                        HoldemCard.E0,
                                        HoldemCard.E0
                                    )).mapToJson {
                                    mapOfJson("src" to it.name.toJson())
                                },
                                "money" to roomPlayer.money.toJson(),
                                "betMoney" to roomPlayer.betMoney.toJson(),
                                "action" to roomPlayer.action.name.toJson()
                            )
                        } ?: JsonObject(emptyMap())),
                        "players" to (table.players.list.mapToJson { roomPlayer ->
                            mapOfJson(
                                "name" to roomPlayer.client.name.toJson(),
                                "uid" to roomPlayer.client.uid.toJson(),
                                "hand" to (if (roomPlayer.hand.isEmpty()) emptyList() else roomPlayer.hand.takeIf { (roomPlayer.client.uid == uid || roomPlayer.currentTable?.phase == HoldemPhase.Showdown || table.players.areAllDone) && roomPlayer.hand.isNotEmpty() }
                                    ?: listOf(
                                        HoldemCard.E0,
                                        HoldemCard.E0
                                    )).mapToJson {
                                    mapOfJson("src" to it.name.toJson())
                                },
                                "money" to roomPlayer.money.toJson(),
                                "betMoney" to roomPlayer.betMoney.toJson(),
                                "action" to roomPlayer.action.name.toJson(),
                                "tags" to listOfNotNull(
                                    if (table.players.dealer?.client?.uid == roomPlayer.client.uid) "dealer" else null,
                                    if (table.pots.any { it.winningPlayers.contains(roomPlayer) }) "winner" else null,
                                    if (table.players.current?.client?.uid == roomPlayer.client.uid) "current" else null,
                                    if (table.players.smallBlinder?.client?.uid == roomPlayer.client.uid) "smallBlind" else null,
                                    if (table.players.largeBlinder?.client?.uid == roomPlayer.client.uid) "largeBlind" else null
                                ).mapToJson { it.toJson() }
                            )
                        }),
                        "actions" to (
                                if (table.players.areAllDone) {
                                    listOfNotNull(
                                        "Leave".takeIf { player?.let { table.players.list.contains(it) } ?: false },
                                        "Continue".takeIf { player?.isCurrent == true }
                                    )
                                } else {
                                    listOfNotNull(
                                        "Leave".takeIf { player?.let { table.players.list.contains(it) } ?: false },
                                        "Fold".takeIf { player?.isCurrent == true && table.phase !is HoldemPhase.Showdown && table.phase !is HoldemPhase.Finish },
                                        "Check".takeIf { player?.isCurrent == true && table.highestBet == 0 && table.phase !is HoldemPhase.Showdown && table.phase !is HoldemPhase.Finish },
                                        "Call".takeIf { player?.isCurrent == true && table.highestBet > 0 && table.phase !is HoldemPhase.Showdown && table.phase !is HoldemPhase.Finish },
                                        "Bet".takeIf { player?.isCurrent == true && table.highestBet == 0 && table.phase !is HoldemPhase.Showdown && table.phase !is HoldemPhase.Finish },
                                        "Raise".takeIf { player?.isCurrent == true && table.highestBet > 0 && table.phase !is HoldemPhase.Showdown && table.phase !is HoldemPhase.Finish },
                                        "AllIn".takeIf { player?.isCurrent == true && table.phase !is HoldemPhase.Showdown && table.phase !is HoldemPhase.Finish }
                                    )
                                }.takeIf { !table.room.lockAction }.orEmpty()
                                ).mapToJson { it.toJson() }
                    )
                } ?: JsonNull)
            ).toString()
        )
    }
}