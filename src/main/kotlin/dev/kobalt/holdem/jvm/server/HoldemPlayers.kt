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

class HoldemPlayers {

    val list: MutableList<HoldemPlayer> = mutableListOf()

    private var dealerUid: String? = list.firstOrNull()?.client?.uid
    private var currentUid: String? = list.firstOrNull()?.client?.uid
    private var winnerUid: String? = null

    var dealer: HoldemPlayer?
        get() = list.find { it.client.uid == dealerUid }
        set(value) {
            dealerUid = value?.client?.uid
        }

    val smallBlinder: HoldemPlayer?
        get() = dealer?.let { playerAfter(it) }

    val largeBlinder: HoldemPlayer?
        get() = smallBlinder?.let { playerAfter(it) }

    var current: HoldemPlayer?
        get() = list.find { it.client.uid == currentUid } // ?: throw Exception("Current player does not exist.")
        set(value) {
            currentUid = value?.client?.uid
        }

    val winner: HoldemPlayer?
        get() = list.takeIf { it.size == 1 }?.first()

    val next get() = list.getOrNull(list.indexOf(current) + 1) ?: list.first()

    fun playerAfter(oldPlayer: HoldemPlayer): HoldemPlayer {
        val players = list
        return players.getOrNull(players.indexOf(oldPlayer) + 1) ?: players.first()
    }

    val nextPlayable: HoldemPlayer
        get() {
            val players = list
            val maxCounter = players.size
            var counter = 0
            var player: HoldemPlayer = current ?: throw Exception("No current player.")
            do {
                if (counter > maxCounter) throw Exception("No players are available.")
                player = players.getOrNull(players.indexOf(player) + 1) ?: players.first()
                counter += 1
            } while (!player.canPlay)
            return player
        }

    fun add(player: HoldemPlayer) {
        list += player
    }

    fun remove(player: HoldemPlayer) {
        list -= player
    }

    inline fun forEach(consumer: (HoldemPlayer) -> Unit) {
        list.forEach(consumer)
    }

    fun indexOf(element: HoldemPlayer): Int {
        return list.indexOf(element)
    }

    operator fun get(index: Int): HoldemPlayer {
        return list[index]
    }

    fun clearBusted() {
        val players = list
        val maxCounter = players.size
        var counter = 0
        var player: HoldemPlayer = current ?: throw Exception("No current player.")
        do {
            if (counter > maxCounter) throw Exception("No players are available.")
            player = players.getOrNull(players.indexOf(player) + 1) ?: players.first()
            counter += 1
        } while (player.money == 0)
        currentUid = player.client.uid

        list.removeIf { it.money == 0 }
    }

    val size: Int get() = list.size


    val areAllDone get() = list.all { it.hasFinishedAction }

    val highestBet get() = list.maxOfOrNull { it.betMoney } ?: 0


}