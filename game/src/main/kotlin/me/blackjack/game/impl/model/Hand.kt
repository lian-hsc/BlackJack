package me.blackjack.game.impl.model

import me.blackjack.terminal.bg
import me.blackjack.terminal.bold
import me.blackjack.terminal.italic
import me.blackjack.terminal.rgb

internal data class Hand(
    private val cards: MutableList<Card> = mutableListOf(),
    var splits: Int = 0,
    var doubled: Boolean = false,
    var surrendered: Boolean = false,
) : MutableList<Card> by cards {

    private val additionalInformation: String
        get() = buildString {
            if (isBlackjack) {
                append(", ")
                append("Blackjack".bg("FFAA00").bold())
            }

            if (isBust) {
                append(", ")
                append("Bust".italic())
            }

            if (doubled) {
                append(", ")
                append("Doubled".rgb("FFAA00").bold())
            }

            if (surrendered) {
                append(", ")
                append("Surrendered".italic())
            }
        }

    val isBlackjack
        get() = splits == 0 && size == 2 && value == 21

    val isBust
        get() = value > 21

    override fun toString(): String = cards.joinToString(" ") + " (${cards.value}$additionalInformation)"

}