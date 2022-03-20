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

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class HoldemPlayer(
    val client: HoldemClient
) {

    val currentRoom get() = client.currentRoom

    val currentTable get() = currentRoom?.table

    val isCurrent get() = currentTable?.players?.current?.client?.uid == client.uid

    suspend fun passTurnToNextPlayer() {
        if (isCurrent) currentTable?.passTurnToNextPlayer()
    }

    suspend fun fold() {
        when {
            currentTable?.players?.winner != null -> sendErrorMessage("Match is over.")
            !isCurrent -> sendErrorMessage("You are not current player.")
            else -> {
                sendLogMessage("${client.uid} - Fold")
                action = HoldemAction.Fold; passTurnToNextPlayer()
                client.currentRoom?.clients?.forEach { it.reload() }
            }
        }
    }

    suspend fun check() {
        when {
            currentTable?.players?.winner != null -> sendErrorMessage("Match is over.")
            !isCurrent -> sendErrorMessage("You are not current player.")
            currentTable?.highestBet != 0 -> sendErrorMessage("Cannot check if there is a bet.")
            else -> {
                sendLogMessage("${client.uid} - Check 0")
                action = HoldemAction.Check; passTurnToNextPlayer()
                client.currentRoom?.clients?.forEach { it.reload() }
            }
        }
    }

    suspend fun call() {
        currentTable?.highestBet.let { currentBet ->
            when {
                currentTable?.players?.winner != null -> sendErrorMessage("Match is over.")
                !isCurrent -> sendErrorMessage("You are not current player.")
                currentBet == null -> sendErrorMessage("Cannot call without any bet.")
                currentBet == 0 -> sendErrorMessage("Cannot call without any bet.")
                money < currentBet -> sendErrorMessage("Not enough money to call.")
                else -> {
                    sendLogMessage("${client.uid} - Call $currentBet")
                    action = HoldemAction.Call; betMoney = currentBet; passTurnToNextPlayer()
                    client.currentRoom?.clients?.forEach { it.reload() }
                }
            }
        }
    }

    suspend fun bet(value: Int?) {
        when {
            currentTable?.players?.winner != null -> sendErrorMessage("Match is over.")
            !isCurrent -> sendErrorMessage("You are not current player.")
            value == null -> sendErrorMessage("Invalid value.")
            currentTable?.highestBet != 0 -> sendErrorMessage("Cannot bet when a bet already exists.")
            currentTable?.blind?.let { it < value } != true -> sendErrorMessage("Bet is too low.")
            money < value -> sendErrorMessage("Not enough money to bet.")
            else -> {
                sendLogMessage("${client.uid} - Call $value")
                action = HoldemAction.Bet; betMoney = value; passTurnToNextPlayer()
                client.currentRoom?.clients?.forEach { it.reload() }
            }
        }
    }

    suspend fun raise(value: Int?) {
        when {
            currentTable?.players?.winner != null -> sendErrorMessage("Match is over.")
            !isCurrent -> sendErrorMessage("You are not current player.")
            value == null -> sendErrorMessage("Invalid value.")
            currentTable?.highestBet == 0 -> sendErrorMessage("Cannot raise without any bet.")
            (currentTable?.highestBet
                ?: 0) * 2 > value -> sendErrorMessage("Cannot raise unless value is doubled value of highest bet.")
            money < value -> sendErrorMessage("Not enough money to raise.")
            else -> {
                sendLogMessage("${client.uid} - Raise $value")
                action = HoldemAction.Raise; betMoney = value; passTurnToNextPlayer()
                client.currentRoom?.clients?.forEach { it.reload() }
            }
        }
    }

    suspend fun allIn() {
        when {
            currentTable?.players?.winner != null -> sendErrorMessage("Match is over.")
            !isCurrent -> sendErrorMessage("You are not current player.")
            else -> {
                sendLogMessage("${client.uid} - All in $money")
                action = HoldemAction.AllIn; betMoney = money; passTurnToNextPlayer()
                client.currentRoom?.clients?.forEach { it.reload() }
            }
        }
    }

    fun getBestHand(): HoldemHand {
        return getHandCombinations().maxByOrNull { it.category.id }!!
    }

    var money: Int = 0
    var betMoney: Int = 0

    val hand: MutableList<HoldemCard> = mutableListOf()

    var action: HoldemAction = HoldemAction.NoAction

    val canPlay get() = !(action is HoldemAction.Fold || action is HoldemAction.AllIn)

    val hasFinishedAction: Boolean
        get() {
            val didAction = action !is HoldemAction.NoAction
            val didCall = (betMoney == currentTable?.highestBet)
            val didAllIn = (action is HoldemAction.AllIn)
            val didFold = (action is HoldemAction.Fold)
            return didAction && (didCall || didAllIn || didFold)
        }

    suspend fun send(value: String) {
        client.session.send(value)
    }

    suspend fun sendErrorMessage(value: String) {
        send(JsonObject(mapOf("message" to JsonPrimitive(value))).toString())
    }

    suspend fun sendLogMessage(value: String) {
        send(JsonObject(mapOf("log" to JsonPrimitive(value))).toString())
    }

    val fullHand: List<HoldemCard> get() = hand + client.currentRoom?.table?.hand.orEmpty()

    fun getHandCombinations(): List<HoldemHand> {
        val combinations = mutableListOf<HoldemHand>()
        fullHand.forEach { firstFilteredCard ->
            fullHand.forEach { secondFilteredCard ->
                val hand = fullHand.filter { it.id != firstFilteredCard.id && it.id != secondFilteredCard.id }
                    .sortedBy { it.suit }
                    .sortedBy { it.value }
                combinations.add(
                    when {
                        isRoyalFlush(hand) -> HoldemHand(hand, HoldemHandCategory.RoyalFlush)
                        isStraightFlush(hand) -> HoldemHand(hand, HoldemHandCategory.StraightFlush)
                        isFourOfAKind(hand) -> HoldemHand(hand, HoldemHandCategory.FourOfAKind)
                        isFullHouse(hand) -> HoldemHand(hand, HoldemHandCategory.FullHouse)
                        isFlush(hand) -> HoldemHand(hand, HoldemHandCategory.Flush)
                        isStraight(hand) -> HoldemHand(hand, HoldemHandCategory.Straight)
                        isThreeOfAKind(hand) -> HoldemHand(hand, HoldemHandCategory.ThreeOfAKind)
                        isTwoPair(hand) -> HoldemHand(hand, HoldemHandCategory.TwoPair)
                        isPair(hand) -> HoldemHand(hand, HoldemHandCategory.Pair)
                        else -> HoldemHand(hand, HoldemHandCategory.HighCard)
                    }
                )
            }
        }
        return combinations.toList()
    }

    fun getPairs(): List<Pair<HoldemCard, HoldemCard>> {
        HoldemCard.valueRange.map { value -> fullHand.filter { it.value == value } }.filter { it.size >= 2 }

        return emptyList()
    }

    /** Reference: http://www.mathcs.emory.edu/~cheung/Courses/170/Syllabus/10/pokerCheck.html */

    fun isFlush(hand: List<HoldemCard>): Boolean {
        if (hand.size != 5) return false
        return hand.map { it.suit }.distinct().size == 1
    }

    fun isStraight(hand: List<HoldemCard>): Boolean {
        if (hand.size != 5) return false
        val sortedHand = hand.sortedBy { it.value }
        if (sortedHand.last().value == 14) {
            val aceLowStraight =
                sortedHand[0].value == 2 && sortedHand[1].value == 3 && sortedHand[2].value == 4 && sortedHand[3].value == 5
            val aceHighStraight =
                sortedHand[0].value == 10 && sortedHand[1].value == 11 && sortedHand[2].value == 12 && sortedHand[3].value == 13
            return aceLowStraight || aceHighStraight
        } else {
            var currentValue = sortedHand.first().value + 1
            sortedHand.minus(sortedHand.first()).forEach {
                if (it.value != currentValue) return false else currentValue += 1
            }
            return true
        }
    }

    fun isStraightFlush(hand: List<HoldemCard>): Boolean {
        return isFlush(hand) && isStraight(hand)
    }

    fun isRoyalFlush(hand: List<HoldemCard>): Boolean {
        return isFlush(hand) && isStraight(hand) && (hand.sortedBy { it.value }.last().value == 14)
    }

    fun isFourOfAKind(hand: List<HoldemCard>): Boolean {
        if (hand.size != 5) return false
        val sortedHand = hand.sortedBy { it.value }
        val lowKind =
            sortedHand[0].value == sortedHand[1].value && sortedHand[1].value == sortedHand[2].value && sortedHand[2].value == sortedHand[3].value
        val highKind =
            sortedHand[1].value == sortedHand[2].value && sortedHand[2].value == sortedHand[3].value && sortedHand[3].value == sortedHand[4].value
        return lowKind || highKind
    }

    fun isFullHouse(hand: List<HoldemCard>): Boolean {
        if (hand.size != 5) return false
        val sortedHand = hand.sortedBy { it.value }
        val lowKind =
            sortedHand[0].value == sortedHand[1].value && sortedHand[1].value == sortedHand[2].value && sortedHand[3].value == sortedHand[4].value
        val highKind =
            sortedHand[0].value == sortedHand[1].value && sortedHand[2].value == sortedHand[3].value && sortedHand[3].value == sortedHand[4].value
        return lowKind || highKind
    }

    fun isThreeOfAKind(hand: List<HoldemCard>): Boolean {
        if (hand.size != 5) return false
        if (isFourOfAKind(hand) || isFullHouse(hand)) return false
        val sortedHand = hand.sortedBy { it.value }
        val lowKind = sortedHand[0].value == sortedHand[1].value && sortedHand[1].value == sortedHand[2].value
        val midKind = sortedHand[1].value == sortedHand[2].value && sortedHand[2].value == sortedHand[3].value
        val highKind = sortedHand[2].value == sortedHand[3].value && sortedHand[3].value == sortedHand[4].value
        return lowKind || midKind || highKind
    }

    fun isTwoPair(hand: List<HoldemCard>): Boolean {
        if (hand.size != 5) return false
        if (isFourOfAKind(hand) || isFullHouse(hand) || isThreeOfAKind(hand)) return false
        val sortedHand = hand.sortedBy { it.value }
        val lowKind = sortedHand[0].value == sortedHand[1].value && sortedHand[2].value == sortedHand[3].value
        val midKind = sortedHand[0].value == sortedHand[1].value && sortedHand[3].value == sortedHand[4].value
        val highKind = sortedHand[1].value == sortedHand[2].value && sortedHand[3].value == sortedHand[4].value
        return lowKind || midKind || highKind
    }

    fun isPair(hand: List<HoldemCard>): Boolean {
        if (hand.size != 5) return false
        if (isFourOfAKind(hand) || isFullHouse(hand) || isThreeOfAKind(hand) || isTwoPair(hand)) return false
        val sortedHand = hand.sortedBy { it.value }
        val lowKind = sortedHand[0].value == sortedHand[1].value
        val midLowKind = sortedHand[1].value == sortedHand[2].value
        val midHighKind = sortedHand[2].value == sortedHand[3].value
        val highKind = sortedHand[3].value == sortedHand[4].value
        return lowKind || midLowKind || midHighKind || highKind
    }
}

