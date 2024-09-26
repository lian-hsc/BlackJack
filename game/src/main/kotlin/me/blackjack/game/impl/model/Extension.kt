package me.blackjack.game.impl.model

internal val List<Card>.value: Int
    get() {
        var value = sumOf { it.rank.value }
        val aces = count { it.rank == Card.CardRank.ACE }

        for (i in 1..aces) {
            if (value > 21) value -= 10
            else break
        }

        return value
    }