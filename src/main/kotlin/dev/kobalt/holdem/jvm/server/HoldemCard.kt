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

enum class HoldemCard(
    var id: Int,
    var suit: Int,
    var value: Int,
    var image: String
) {
    E0(-1, -1, -1, "1B"),
    C2(0, 0, 2, "2C"),
    C3(1, 0, 3, "3C"),
    C4(2, 0, 4, "4C"),
    C5(3, 0, 5, "5C"),
    C6(4, 0, 6, "6C"),
    C7(5, 0, 7, "7C"),
    C8(6, 0, 8, "8C"),
    C9(7, 0, 9, "9C"),
    CT(8, 0, 10, "TC"),
    CJ(9, 0, 11, "JC"),
    CQ(10, 0, 12, "QC"),
    CK(11, 0, 13, "KC"),
    CA(12, 0, 14, "AC"),
    D2(13, 1, 2, "2D"),
    D3(14, 1, 3, "3D"),
    D4(15, 1, 4, "4D"),
    D5(16, 1, 5, "5D"),
    D6(17, 1, 6, "6D"),
    D7(18, 1, 7, "7D"),
    D8(19, 1, 8, "8D"),
    D9(20, 1, 9, "9D"),
    DT(21, 1, 10, "TD"),
    DJ(22, 1, 11, "JD"),
    DQ(23, 1, 12, "QD"),
    DK(24, 1, 13, "KD"),
    DA(25, 1, 14, "AD"),
    S2(26, 2, 2, "2S"),
    S3(27, 2, 3, "3S"),
    S4(28, 2, 4, "4S"),
    S5(29, 2, 5, "5S"),
    S6(30, 2, 6, "6S"),
    S7(31, 2, 7, "7S"),
    S8(32, 2, 8, "8S"),
    S9(33, 2, 9, "9S"),
    ST(34, 2, 10, "TS"),
    SJ(35, 2, 11, "JS"),
    SQ(36, 2, 12, "QS"),
    SK(37, 2, 13, "KS"),
    SA(38, 2, 14, "AS"),
    H2(39, 3, 2, "2H"),
    H3(40, 3, 3, "3H"),
    H4(41, 3, 4, "4H"),
    H5(42, 3, 5, "5H"),
    H6(43, 3, 6, "6H"),
    H7(44, 3, 7, "7H"),
    H8(45, 3, 8, "8H"),
    H9(46, 3, 9, "9H"),
    HT(47, 3, 10, "TH"),
    HJ(48, 3, 11, "JH"),
    HQ(49, 3, 12, "QH"),
    HK(50, 3, 13, "KH"),
    HA(51, 3, 14, "AH");

    companion object {
        val valueRange = (2..14)
    }

    val string: String
        get() {
            val suitString = when (suit) {
                0 -> "Clubs"
                1 -> "Diamonds"
                2 -> "Spades"
                3 -> "Hearts"
                else -> "Error"
            }
            val valueString = when (value) {
                in 2..10 -> value.toString()
                11 -> "Jack"
                12 -> "Queen"
                13 -> "King"
                14 -> "Ace"
                else -> "Error"
            }
            return "$valueString of $suitString"
        }

}