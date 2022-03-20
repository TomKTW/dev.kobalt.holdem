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

import dev.kobalt.holdem.jvm.extension.randomUid
import dev.kobalt.holdem.jvm.extension.syncedHashSet


class HoldemServer {

    val clients: MutableSet<HoldemClient> = syncedHashSet()
    val rooms: MutableSet<HoldemRoom> = syncedHashSet()

    companion object {
        const val attemptLimit = 10
    }

    fun generateClientUid(): String {
        var attempt = 0
        do String.randomUid()
            .let { uid -> if (clients.any { it.uid == uid }) attempt += 1 else return uid } while (attempt < attemptLimit)
        throw Exception()
    }

    fun generateRoomUid(): String {
        var attempt = 0
        do String.randomUid().let { uid ->
            if (rooms.any { it.uid == uid }) attempt += 1 else return uid
        } while (attempt < attemptLimit)
        throw Exception()
    }

}