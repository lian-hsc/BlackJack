package me.blackjack.game.impl.model

internal data class Deck(private val decks: Int, private val shufflePercentage: Double) {

    private val maxCards = decks * 52

    private val cards = mutableListOf<Card>()

    val shouldShuffle
        get() = cards.size < maxCards * shufflePercentage

    init {
        shuffle()
    }

    fun shuffle() {
        cards.clear()
        repeat(decks) { cards.addAll(Card.fullDeck) }
        cards.shuffle()
    }

    fun draw(): Card = cards.removeFirst()

}