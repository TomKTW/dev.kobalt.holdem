package dev.kobalt.holdem.android.state

import dev.kobalt.holdem.android.extension.jsonArrayOrNull
import dev.kobalt.holdem.android.extension.jsonObjectOrNull
import dev.kobalt.holdem.android.extension.jsonPrimitiveOrNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

fun JsonObject.toStateEntity(): StateEntity {
    return StateEntity(
        get("player")?.jsonObjectOrNull?.toStateEntityPlayer(),
        get("currentRoom")?.jsonObjectOrNull?.toStateEntityRoom(),
        get("currentTable")?.jsonObjectOrNull?.toStateEntityTable()
    )
}

fun JsonObject.toStateEntityPlayer(): StateEntity.Player {
    return StateEntity.Player(
        get("uid")?.jsonPrimitiveOrNull?.contentOrNull,
        get("name")?.jsonPrimitiveOrNull?.contentOrNull,
        get("hand")?.jsonArrayOrNull?.mapNotNull { it.jsonObjectOrNull?.toStateEntityCard() }
            .orEmpty(),
        get("money")?.jsonPrimitiveOrNull?.intOrNull,
        get("betMoney")?.jsonPrimitiveOrNull?.intOrNull,
        get("action")?.jsonPrimitiveOrNull?.contentOrNull,
        get("tags")?.jsonArrayOrNull?.mapNotNull { it.jsonPrimitiveOrNull?.contentOrNull }.orEmpty()
    )
}

fun JsonObject.toStateEntityCard(): StateEntity.Card {
    return StateEntity.Card(
        get("src")?.jsonPrimitiveOrNull?.contentOrNull
    )
}

fun JsonObject.toStateEntityRoom(): StateEntity.Room {
    return StateEntity.Room(
        get("uid")?.jsonPrimitiveOrNull?.contentOrNull,
        get("status")?.jsonPrimitiveOrNull?.contentOrNull,
        get("playerLimit")?.jsonPrimitiveOrNull?.intOrNull,
        get("players")?.jsonArrayOrNull?.mapNotNull { it.jsonObjectOrNull?.toStateEntityPlayer() }
            .orEmpty(),
        get("actions")?.jsonArrayOrNull?.mapNotNull { it.jsonPrimitiveOrNull?.contentOrNull }
            .orEmpty()
    )
}

fun JsonObject.toStateEntityPot(): StateEntity.Pot {
    return StateEntity.Pot(
        get("uid")?.jsonPrimitiveOrNull?.contentOrNull,
        get("eligible")?.jsonArrayOrNull?.mapNotNull { it.jsonPrimitiveOrNull?.contentOrNull }
            .orEmpty(),
        get("winning")?.jsonArrayOrNull?.mapNotNull { it.jsonPrimitiveOrNull?.contentOrNull }
            .orEmpty()
    )
}

fun JsonObject.toStateEntityTable(): StateEntity.Table {
    return StateEntity.Table(
        get("highestBet")?.jsonPrimitiveOrNull?.intOrNull,
        get("phase")?.jsonPrimitiveOrNull?.contentOrNull,
        get("pots")?.jsonArrayOrNull?.mapNotNull { it.jsonObjectOrNull?.toStateEntityPot() }
            .orEmpty(),
        get("hand")?.jsonArrayOrNull?.mapNotNull { it.jsonObjectOrNull?.toStateEntityCard() }
            .orEmpty(),
        get("currentPlayer")?.jsonObjectOrNull?.toStateEntityPlayer(),
        get("players")?.jsonArrayOrNull?.mapNotNull { it.jsonObjectOrNull?.toStateEntityPlayer() }
            .orEmpty(),
        get("actions")?.jsonArrayOrNull?.mapNotNull { it.jsonPrimitiveOrNull?.contentOrNull }
            .orEmpty()
    )
}