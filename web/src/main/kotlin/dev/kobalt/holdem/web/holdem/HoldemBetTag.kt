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

package dev.kobalt.web.personal.holdem

import kotlinx.css.CssBuilder
import kotlinx.html.*

@HtmlTagMarker
fun HtmlBlockTag.holdemBetInput() = div {
    style = "display: inline-block;"
    id = "currentTableMoney"
    input {
        id = "currentTableMoneyInput"
        type = InputType.range
        style = CssBuilder().apply {
            put("display", "inline-block")
            put("height", "10px")
        }.toString()
    }
    p {
        id = "currentTableMoneyValue"
        style = CssBuilder().apply {
            put("display", "inline-block")
        }.toString()
    }
}