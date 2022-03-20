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

package dev.kobalt.holdem.jvm

import dev.kobalt.holdem.jvm.extension.ifLet
import dev.kobalt.holdem.jvm.server.HoldemClient
import dev.kobalt.holdem.jvm.server.HoldemServer
import dev.kobalt.holdem.jvm.server.HoldemWebSocketSession
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.websocket.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.slf4j.event.Level
import java.time.Duration

fun main(args: Array<String>) {
    val parser = ArgParser("holdem")
    val httpServerPort by parser.option(ArgType.Int, "httpServerPort", null, null)
    val httpServerHost by parser.option(ArgType.String, "httpServerHost", null, null)

    parser.parse(args)
    ifLet(httpServerPort, httpServerHost) { port, host ->
        startWebServer(port, host)
    }
}


fun startWebServer(port: Int, host: String) {
    embeddedServer(CIO, port, host) {
        install(XForwardedHeaderSupport)
        install(DefaultHeaders)
        install(CachingHeaders)
        install(WebSockets) { pingPeriod = Duration.ofMinutes(1) }
        install(CallLogging) { level = Level.INFO }
        install(Compression) { gzip() }
        install(Routing) {
            val server = HoldemServer()
            webSocket {
                HoldemClient(HoldemWebSocketSession(this), server).let { client ->
                    try {
                        client.server.clients += client
                        client.reload()
                        incoming.consumeEach { frame ->
                            when (frame) {
                                is Frame.Text -> frame.readText().split(" ").also {
                                    launch { client.onAction(it[0], it.drop(1)) }
                                }
                                else -> return@consumeEach
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        client.leaveRoom()
                        client.server.clients -= client
                    }
                }
            }
        }
    }.start(true)
}

