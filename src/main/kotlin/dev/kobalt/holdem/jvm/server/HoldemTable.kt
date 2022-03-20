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

class HoldemTable(
    val room: HoldemRoom,
    val players: HoldemPlayers,
    val deck: HoldemDeck,
    val hand: HoldemTableHand,
    val pots: HoldemPots,
    var blind: Int,
    var phase: HoldemPhase
) {

    val smallBlind get() = blind
    val largeBlind get() = blind * 2

    suspend fun payout() {
        room.reloadClients()
        pots.forEach { pot ->
            val split = pot.amount.div(pot.winningPlayers.size)
            pot.winningPlayers.forEach { player ->
                players.forEach { it.sendLogMessage("Player awarded: ${player.client.uid} + $split") }
                player.money += split
            }
        }
        players.forEach {
            it.action = HoldemAction.NoAction
        }
        room.reloadClients(msDelay = 2500)
        players.clearBusted()
        if (players.winner != null) {
            hand.clear()
            pots.clear()
            players.forEach { player -> player.hand.clear() }
            phase = HoldemPhase.Finish
        } else {
            start()
        }
    }

    suspend fun passTurnToNextPlayer() {
        if (!room.lockAction) {
            room.lockAction = true
            if (!players.areAllDone) {
                val notFoldedPlayers = players.list.filter { it.action != HoldemAction.Fold }
                if (notFoldedPlayers.size == 1) {
                    val notFoldedPlayer = notFoldedPlayers.first()
                    // Create new pot for this phase.
                    createPot()
                    pots.forEach {
                        if (!it.eligiblePlayers.contains(notFoldedPlayer)) it.eligiblePlayers.add(
                            notFoldedPlayer
                        )
                    }
                    pots.forEach { it.winningPlayers.add(notFoldedPlayer) }
                    // Reset action for each player, unless they have folded or went all in.
                    players.forEach {
                        if (it.action != HoldemAction.AllIn && it.action != HoldemAction.Fold) it.action =
                            HoldemAction.NoAction
                    }
                    room.reloadClients()
                    payout()
                } else {
                    players.current = players.nextPlayable
                }
            } else {
                val notFoldedPlayers = players.list.filter { it.action != HoldemAction.Fold }
                if (notFoldedPlayers.size == 1) {
                    val notFoldedPlayer = notFoldedPlayers.first()
                    // Create new pot for this phase.
                    createPot()
                    pots.forEach {
                        if (!it.eligiblePlayers.contains(notFoldedPlayer)) it.eligiblePlayers.add(
                            notFoldedPlayer
                        )
                    }
                    pots.forEach { it.winningPlayers.add(notFoldedPlayer) }
                    // Reset action for each player, unless they have folded or went all in.
                    players.forEach {
                        if (it.action != HoldemAction.AllIn && it.action != HoldemAction.Fold) it.action =
                            HoldemAction.NoAction
                    }
                    room.reloadClients()
                    payout()
                } else {
                    // Create new pot for this phase.
                    createPot()
                    // Reset action for each player, unless they have folded or went all in.
                    players.forEach {
                        if (it.action != HoldemAction.AllIn && it.action != HoldemAction.Fold) it.action =
                            HoldemAction.NoAction
                    }
                    room.reloadClients()
                    // Prepare next turn.
                    beginNextTurn()

                    if (!players.areAllDone) {
                        players.current = players.nextPlayable
                    } else {
                        while (phase != HoldemPhase.Showdown && phase != HoldemPhase.Finish) {
                            room.reloadClients(250)
                            beginNextTurn()
                        }
                    }
                    room.reloadClients(1000)
                }
            }
            room.lockAction = false
        }/*
        if (!players.areAllDone && phase != HoldemPhase.Showdown) {
            players.current = players.nextPlayable
        } else {
            nextPhase()
            if (!players.areAllDone) {
                players.current = players.nextPlayable
            } /* Automated */ /*else {
                while (phase != HoldemPhase.Showdown) {
                    room.clients.forEach { it.reload() }
                    delay(1000)
                    nextPhase()
                }
            }*/
        }
        */
    }

    suspend fun beginNextTurn() {
        val oldPhase = phase
        phase = when (oldPhase) {
            is HoldemPhase.Initial -> TODO()
            is HoldemPhase.Compulsory -> HoldemPhase.Preflop
            is HoldemPhase.Preflop -> HoldemPhase.Flop
            is HoldemPhase.Flop -> HoldemPhase.Turn
            is HoldemPhase.Turn -> HoldemPhase.River
            is HoldemPhase.River -> HoldemPhase.Showdown
            is HoldemPhase.Showdown -> HoldemPhase.Compulsory
            is HoldemPhase.Finish -> TODO()
        }

        when (phase) {
            HoldemPhase.Initial -> {
            }
            HoldemPhase.Compulsory -> {
            }
            HoldemPhase.Preflop -> {
            }
            HoldemPhase.Flop -> {
                repeat(3) {
                    hand += deck.draw()
                    room.reloadClients(msDelay = 500)
                }
            }
            HoldemPhase.Turn -> {
                hand += deck.draw()
                room.reloadClients(msDelay = 500)
            }
            HoldemPhase.River -> {
                hand += deck.draw()
                room.reloadClients(msDelay = 500)
            }
            HoldemPhase.Showdown -> {
                room.reloadClients(msDelay = 500)
                pots.forEach { pot ->
                    pot.eligiblePlayers.map { it to it.getBestHand() }
                        .maxByOrNull { it.second.category.id }?.first?.let {
                            pot.winningPlayers.add(it)
                        }
                }
                room.reloadClients(msDelay = 1000)
                payout()
            }
            HoldemPhase.Finish -> {

            }
        }
    }

    suspend fun createPot() {
        var amount = 0
        val eligiblePlayers = players.list.filter {
            when (it.action) {
                is HoldemAction.Check,
                is HoldemAction.Call,
                is HoldemAction.Bet,
                is HoldemAction.Raise -> true
                is HoldemAction.AllIn -> it.betMoney >= 0
                else -> false
            }
        }
        players.list.onEach {
            amount += it.betMoney
            it.money -= it.betMoney
            it.betMoney = 0
        }
        val newPot = HoldemPot(eligiblePlayers.toMutableList(), mutableListOf(), amount)
        players.forEach { it.sendLogMessage("Created pot: ${newPot.amount}") }
        pots.lastOrNull()?.takeIf { it.eligiblePlayers.containsAll(newPot.eligiblePlayers) }?.also { pot ->
            players.forEach { it.sendLogMessage("Merged pot: ${pot.amount} + ${newPot.amount}") }
            pot.amount += newPot.amount
        } ?: run {
            players.forEach { it.sendLogMessage("Added pot: ${newPot.amount}") }
            pots.add(newPot)
        }
    }

    suspend fun start() {
        // Set compulsory phase.
        phase = HoldemPhase.Compulsory
        // Clear all hands and pots.
        hand.clear()
        pots.clear()
        players.forEach { it.hand.clear() }
        // Construct the deck.
        deck.construct()
        room.reloadClients(msDelay = 500)
        // Define the dealer.
        players.dealer = players.dealer?.let { players.playerAfter(it) } ?: players.list.first()
        room.reloadClients(msDelay = 500)
        // Apply small blind.
        players.smallBlinder?.betMoney = smallBlind
        room.reloadClients(msDelay = 500)
        // Apply large blind.
        players.largeBlinder?.betMoney = largeBlind
        room.reloadClients(msDelay = 500)
        // Define current player.
        players.current = players.largeBlinder?.let { players.playerAfter(it) }
        room.reloadClients(msDelay = 500)
        // Draw hole cards for each player.
        players.forEach { player ->
            repeat(2) { player.hand.add(deck.draw()) }
            room.reloadClients(msDelay = 500)
        }
        phase = HoldemPhase.Preflop
        room.reloadClients()
    }

    val highestBet get() = players.highestBet

}