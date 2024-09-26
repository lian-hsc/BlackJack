package me.blackjack.game.impl.model

internal data class Hand(
    private val cards: MutableList<Card> = mutableListOf(),
    var splits: Int = 0,
    var doubled: Boolean = false,
    var surrendered: Boolean = false,
) : MutableList<Card> by cards {

    val isBlackjack
        get() = splits == 0 && size == 2 && value == 21

    val isBust
        get() = value > 21

}