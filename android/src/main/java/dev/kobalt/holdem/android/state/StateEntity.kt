package dev.kobalt.holdem.android.state

data class StateEntity(
    val player: Player?,
    val currentRoom: Room?,
    val currentTable: Table?
) {

    data class Player(
        val uid: String?,
        val name: String?,
        val hand: List<Card>,
        val money: Int?,
        val betMoney: Int?,
        val action: String?,
        val tags: List<String>
    )

    data class Card(
        val src: String?
    )

    data class Room(
        val uid: String?,
        val status: String?,
        val playerLimit: Int?,
        val players: List<Player>,
        val actions: List<String>
    )

    data class Pot(
        val amount: String?,
        val eligible: List<String>,
        val winning: List<String>
    )

    data class Table(
        val highestBet: Int?,
        val phase: String?,
        val pots: List<Pot>,
        val hand: List<Card>,
        val currentPlayer: Player?,
        val players: List<Player>,
        val actions: List<String>
    )

}