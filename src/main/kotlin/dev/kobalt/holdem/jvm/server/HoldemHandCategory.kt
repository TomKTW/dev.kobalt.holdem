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

sealed class HoldemHandCategory(
    var id: Int,
    var name: String
) {
    object None : HoldemHandCategory(0, "None")
    object HighCard : HoldemHandCategory(1, "High Card")
    object Pair : HoldemHandCategory(2, "Pair")
    object TwoPair : HoldemHandCategory(3, "Two Pair")
    object ThreeOfAKind : HoldemHandCategory(4, "Three of a Kind")
    object Straight : HoldemHandCategory(5, "Straight")
    object Flush : HoldemHandCategory(6, "Flush")
    object FullHouse : HoldemHandCategory(7, "Full House")
    object FourOfAKind : HoldemHandCategory(8, "Four Of A Kind")
    object StraightFlush : HoldemHandCategory(9, "Straight Flush")
    object RoyalFlush : HoldemHandCategory(10, "Royal Flush")
}