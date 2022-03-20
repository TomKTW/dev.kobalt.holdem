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

sealed class HoldemPhase(val name: String) {
    object Initial : HoldemPhase("Initial")
    object Compulsory : HoldemPhase("Compulsory")
    object Preflop : HoldemPhase("Preflop")
    object Flop : HoldemPhase("Flop")
    object Turn : HoldemPhase("Turn")
    object River : HoldemPhase("River")
    object Showdown : HoldemPhase("Showdown")
    object Finish : HoldemPhase("Finish")
}