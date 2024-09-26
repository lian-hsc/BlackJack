package me.blackjack.game.impl.model

import me.blackjack.terminal.rgb


internal data class Card(val suit: CardSuit, val rank: CardRank) {

    override fun toString(): String = "${suit.symbol}${rank.symbol}".rgb(suit.hex)

    enum class CardSuit(val symbol: String, val hex: String) {
        SPADES("♠", "AAAAAA"),
        HEARTS("♥", "FF5555"),
        DIAMONDS("♦", "FF5555"),
        CLUBS("♣", "AAAAAA");
    }


    enum class CardRank(val symbol: String, val value: Int) {
        ACE("A", 11),
        KING("K", 10),
        QUEEN("Q", 10),
        JACK("J", 10),
        TEN("T", 10),
        NINE("9", 9),
        EIGHT("8", 8),
        SEVEN("7", 7),
        SIX("6", 6),
        FIVE("5", 5),
        FOUR("4", 4),
        THREE("3", 3),
        TWO("2", 2);
    }

    companion object {

        val fullDeck by lazy {
            CardSuit.entries.flatMap { suit -> CardRank.entries.map { rank -> Card(suit, rank) } }
        }

    }

}