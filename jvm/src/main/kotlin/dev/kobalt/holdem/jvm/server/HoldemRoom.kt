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

import dev.kobalt.holdem.jvm.extension.syncedHashSet
import kotlinx.coroutines.delay

class HoldemRoom(
    server: HoldemServer,
    var ownerUid: String,
    var clientsLimit: Int,
    var table: HoldemTable?
) {

    val uid = server.generateRoomUid()

    var lockAction = false

    val clients = syncedHashSet<HoldemClient>()

    val owner: HoldemClient?
        get() = clients.find { it.uid == ownerUid }

    val status: HoldemStatus
        get() = when {
            clients.size >= clientsLimit -> HoldemStatus.Full
            else -> HoldemStatus.Open
        }

    suspend fun start() {
        if (!lockAction) {
            lockAction = true
            val clients = clients
            table = HoldemTable(
                room = this,
                players = HoldemPlayers(),
                deck = HoldemDeck(),
                hand = HoldemTableHand(),
                pots = HoldemPots(),
                blind = 50,
                phase = HoldemPhase.Initial
            )
            table?.apply {
                clients.map { HoldemPlayer(it).also { it.money = 1000 } }.forEach { player ->
                    players.add(player)
                    if (players.list.size == 1) {
                        players.dealer = player
                    }
                }
                reloadClients()
                start()
            }
            lockAction = false
            clients.forEach { it.reload() }
        }
    }

    suspend fun reloadClients(msDelay: Long = 0) {
        if (msDelay > 0) {
            delay(msDelay)
        }
        clients.forEach { it.reload() }
    }

}