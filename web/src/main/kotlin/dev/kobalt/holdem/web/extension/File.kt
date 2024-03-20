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

import java.io.File
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageInputStream

internal fun File.imageDimensions(): Pair<Int, Int> {
    if (extension == "png") {
        ImageIO.getImageReadersByFormatName("PNG").forEach { reader ->
            try {
                FileImageInputStream(this).use {
                    reader.input = it
                    val width = reader.getWidth(reader.minIndex)
                    val height = reader.getHeight(reader.minIndex)
                    return Pair(width, height)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                reader.dispose()
            }
        }
    } else if (extension == "gif") {
        ImageIO.getImageReadersByFormatName("gif").forEach { reader ->
            try {
                ImageIO.createImageInputStream(this).use {
                    reader.input = it
                    val width = reader.getWidth(reader.minIndex)
                    val height = reader.getHeight(reader.minIndex)
                    return Pair(width, height)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                reader.dispose()
            }
        }
    }
    return Pair(0, 0)
}

internal fun File.isLocatedIn(parent: File) = canonicalFile.toPath().startsWith(parent.canonicalFile.toPath())

internal fun File.requireIsLocatedIn(parent: File) = takeIf { isLocatedIn(parent) }
    ?: throw Exception("File ${this.canonicalPath} is not located in ${parent.canonicalPath}.")