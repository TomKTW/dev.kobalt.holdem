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

package dev.kobalt.holdem.jvm.uid

import java.nio.ByteBuffer
import java.util.*

@JvmInline
value class Uid(val value: UUID) {

    companion object {
        val none = UUID(0L, 0L).toUid()

        fun random(): Uid {
            return UUID.randomUUID().toString().replace("-", "").toUid()!!
        }
    }

    override fun toString(): String {
        val bb: ByteBuffer = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(value.mostSignificantBits)
        bb.putLong(value.leastSignificantBits)
        return Base64.getUrlEncoder().encodeToString(bb.array()).replace("=", "")
    }

}

fun String.toUid(): Uid? {
    return runCatching {
        val decoded = Base64.getUrlDecoder().decode(this.toByteArray())
        val buffer = ByteBuffer.wrap(decoded)
        return Uid(UUID(buffer.long, buffer.long))
    }.getOrNull()
}

fun UUID.toUid(): Uid {
    return Uid(this)
}