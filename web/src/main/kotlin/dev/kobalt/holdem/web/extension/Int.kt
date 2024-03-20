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

package dev.kobalt.lib.hsp2html.extension

internal fun Int.align(oldRange: IntRange, newRange: IntRange): Int {
    val oldValue = this
    val oldMin = oldRange.minOrNull()!!
    val oldMax = oldRange.maxOrNull()!!
    val newMin = newRange.minOrNull()!!
    val newMax = newRange.maxOrNull()!!
    val oldOffset = oldMax - oldMin
    val newOffset = newMax - newMin
    return if (oldOffset == 0) newMin else (((oldValue - oldMin) * newOffset) / oldOffset) + newMin
}