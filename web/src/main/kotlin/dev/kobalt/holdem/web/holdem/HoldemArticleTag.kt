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

import kotlinx.html.*

@HtmlTagMarker
fun HtmlBlockTag.holdemArticle(
    id: String? = null,
    title: String? = null,
    titleLink: String? = null,
    titleId: String? = null,
    subtitle: String? = null,
    subtitleLink: String? = null,
    subtitleId: String? = null,
    block: (HtmlBlockTag.() -> Unit) = {}
) {
    div("article-parent") {
        id?.let { this.id = it }
        div("article") {
            div("article-header") {
                title?.let {
                    div("article-header-left") {
                        h2 {
                            titleId?.let { this.id = it }
                            titleLink?.let { link -> a(link) { text(it) } } ?: text(it)
                        }
                    }
                }
                subtitle?.let {
                    div("article-header-right") {
                        h2 {
                            subtitleId?.let { this.id = it }
                            subtitleLink?.let { link -> a(link) { text(it) } } ?: text(it)
                        }
                    }
                }
                div("article-header-clear")
            }
            div { block() }
        }
    }
}