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

package dev.kobalt.holdem.web.play

import dev.kobalt.holdem.web.extension.pageArticle
import dev.kobalt.holdem.web.extension.respondHtmlContent
import dev.kobalt.web.personal.credit.credit
import dev.kobalt.web.personal.holdem.holdemArticle
import dev.kobalt.web.personal.holdem.holdemBetInput
import io.ktor.application.*
import io.ktor.routing.*
import kotlinx.css.CssBuilder
import kotlinx.html.*

fun Route.playRoute() {
    route(PlayRepository.pageRoute) {
        get {
            val roomId = call.request.queryParameters["id"].orEmpty()
            call.respondHtmlContent(
                title = PlayRepository.pageTitle,
                description = PlayRepository.pageSubtitle,
                head = {
                    style {
                        unsafe {
                            +CssBuilder().apply {
                                rule(".content-main input") {
                                    put("width", "inherit")
                                }
                                rule(".table") {
                                    put("background", "#222222")
                                    put("margin", "8px")
                                    put("padding", "8px")
                                }
                                rule(".table-hand") {
                                    put("background", "#444444")
                                    put("margin", "8px")
                                    put("padding", "8px")
                                }
                                rule(".player") {
                                    put("background", "#000000")
                                    put("margin", "8px")
                                    put("padding", "8px")
                                }
                                rule(".player-current") {
                                    put("background", "#008800")
                                    put("margin", "8px")
                                    put("padding", "8px")
                                }
                                rule(".player-dealer") {
                                    put("background", "#444444")
                                    put("margin", "8px")
                                    put("padding", "8px")
                                }
                                rule(".player-winner") {
                                    put("background", "#ff8800")
                                    put("margin", "8px")
                                    put("padding", "8px")
                                }
                            }.toString()
                        }
                    }
                }
            ) {
                pageArticle(
                    PlayRepository.pageTitle,
                    PlayRepository.pageSubtitle
                ) {

                    holdemArticle(
                        id = "currentTable",
                        title = "Table",
                        subtitle = "-/-",
                        subtitleId = "currentTableSubtitle"
                    ) {
                        div {
                            ul {
                                id = "currentTableContent"
                                style = "list-style-type: none; margin: 0;"
                            }
                        }
                        div {
                            id = "actions"
                            br { }
                            input { id = "currentTableFold"; value = "Fold"; type = kotlinx.html.InputType.submit }
                            input {
                                id = "currentTableCheck"; value = "Check"; type = kotlinx.html.InputType.submit
                            }
                            input { id = "currentTableCall"; value = "Call"; type = kotlinx.html.InputType.submit }
                            input { id = "currentTableBet"; value = "Bet"; type = kotlinx.html.InputType.submit }
                            input {
                                id = "currentTableRaise"; value = "Raise"; type = kotlinx.html.InputType.submit
                            }
                            input {
                                id = "currentTableAllIn"; value = "All In"; type = kotlinx.html.InputType.submit
                            }
                            input {
                                id = "currentTableLeave"; value = "Leave"; type = kotlinx.html.InputType.submit
                            }
                            holdemBetInput()
                        }
                    }
                    holdemArticle(
                        id = "currentRoom",
                        title = "Room",
                        titleId = "currentRoomTitle",
                        subtitle = "-/-",
                        subtitleId = "currentRoomSubtitle"
                    ) {
                        id = "currentRoomContent"
                        div {
                            h3 { text("ID") }
                            p {
                                id = "currentRoomId"
                            }
                            h3 { text("Players") }
                            p {
                                ul {
                                    id = "currentRoomPlayers"
                                    style = "list-style-type: none; margin: 0;"
                                }
                            }
                            h3 { text("Actions") }
                            p {
                                id = "currentRoomActions"
                            }
                            p {
                                input {
                                    id = "currentRoomLeave"; value = "Leave"; type = kotlinx.html.InputType.submit
                                }
                                unsafe { +"&nbsp;" }
                                input {
                                    id = "currentRoomStart"; value = "Start"; type = kotlinx.html.InputType.submit
                                }
                            }
                        }
                    }
                    holdemArticle(
                        id = "createRoom",
                        title = "Create",
                        titleId = "createRoomTitle",
                        subtitle = "",
                        subtitleId = "createRoomSubtitle"
                    ) {
                        p { text("Player limit") }
                        input { id = "createRoomInput"; type = kotlinx.html.InputType.text; value = "10" }
                        br { }
                        input { id = "createRoomSubmit"; value = "Submit"; type = kotlinx.html.InputType.submit }
                    }
                    holdemArticle(
                        id = "joinRoom",
                        title = "Join",
                        titleId = "joinRoomTitle",
                        subtitle = "",
                        subtitleId = "joinRoomSubtitle"
                    ) {
                        p { text("Room ID") }
                        input { id = "joinRoomInput"; type = kotlinx.html.InputType.text; value = roomId }
                        br { }
                        input { id = "joinRoomSubmit"; value = "Submit"; type = kotlinx.html.InputType.submit }
                    }
                    holdemArticle(
                        id = "player",
                        title = "Player",
                        titleId = "playerTitle",
                        subtitle = "-",
                        subtitleId = "playerSubtitle"
                    ) {
                        id = "playerContent"
                        p { text("Name") }
                        input { id = "playerNameInput"; type = kotlinx.html.InputType.text }
                        br {}
                        input { id = "playerNameSubmit"; value = "Submit"; type = kotlinx.html.InputType.submit }
                    }
                    credit(dev.kobalt.web.personal.holdem.HoldemRepository.credit)
                    script {
                        unsafe {
                            +(dev.kobalt.web.personal.holdem.HoldemRepository.cardScript)
                            +(dev.kobalt.web.personal.holdem.HoldemRepository.engineScript)
                        }
                    }
                }
            }
        }
    }
}
