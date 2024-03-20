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

import dev.kobalt.holdem.web.holdem.HoldemCard
import dev.kobalt.holdem.web.play.PlayRepository
import dev.kobalt.holdem.web.resource.ResourceRepository
import dev.kobalt.web.personal.credit.CreditEntity

object HoldemRepository {

    val credit = CreditEntity(
        product = "SVG Playing Cards",
        productLink = "https://www.me.uk/cards/",
        version = "2.1",
        year = "2016",
        author = "RevK",
        authorLink = "https://www.revk.uk/",
        license = "CC-00-PD-1.0",
        licenseLink = "https://creativecommons.org/publicdomain/zero/1.0/legalcode"
    )

    var websocketPath: String? = null

    val engineScript by lazy {
        ResourceRepository.getText("engine.js").orEmpty()
            .replace("\$websocketServerUrl", PlayRepository.websocketServerUrl.orEmpty())
    }
    val cardScript by lazy {
        StringBuilder().apply {
            append("\n")
            append(HoldemCard.values().map { card ->
                ClassLoader.getSystemClassLoader()
                    .getResourceAsStream("dev/kobalt/holdem/web/card/" + card.image + ".svg")?.use {
                        "var card${card.name} = \"${
                            it.readAllBytes().decodeToString().replace("\n", "").replace("\"", "\\x22")
                        }\""
                    }
            }.joinToString("\n"))
            append("\n\nfunction getCard(value) {\n")
            append(HoldemCard.values().map { card ->
                "if (value == \"${card.name}\") return card${card.name};"
            }.joinToString("\n"))
            append("\nreturn null;\n};\n\n")
        }.toString()
    }

}