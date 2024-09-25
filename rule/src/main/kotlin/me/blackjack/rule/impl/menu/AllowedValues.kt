package me.blackjack.rule.impl.menu

import me.blackjack.rule.Rule

internal data object AllowedValues {

    val decks = mapOf(
        1 to "One Deck",
        2 to "Two Decks",
        4 to "Four Decks",
        6 to "Six Decks",
        8 to "Eight Decks",
    )

    val shufflePercentage = mapOf(
        0.5 to "50%",
        0.6 to "60%",
        0.7 to "70%",
        0.75 to "75%",
        0.8 to "80%",
        0.85 to "85%",
        0.9 to "90%",
        0.95 to "95%",
        1.0 to "100%",
    )

    val blackjackPayout = mapOf(
        1.0 to "1:1",
        1.2 to "6:5",
        1.4 to "7:5",
        1.5 to "3:2",
        2.0 to "2:1",
        3.0 to "3:1",
    )

    private val payout = mapOf(null to "Like blackjack") + blackjackPayout

    private val luckyLuckyPayout = payout + mapOf(
        5.0 to "5:1",
        10.0 to "10:1",
        15.0 to "15:1",
        30.0 to "30:1",
        40.0 to "40:1",
        50.0 to "50:1",
        75.0 to "75:1",
        100.0 to "100:1",
        150.0 to "150:1",
        200.0 to "200:1",
    )

    private val boolean = mapOf(true to "Yes", false to "No")
    
    private val splitAmount = mapOf(
        0 to "No Split",
        1 to "One Split",
        2 to "Two Split",
        3 to "Three Split",
        4 to "Four Split",
        5 to "Five Split",
        null to "Any Split",
    )
    
    @Suppress("UNCHECKED_CAST")
    fun <Type> getAllowedValues(rule: Rule<Type>): Map<Type, String> = when(rule) {
        Rule.General.decks -> decks
        Rule.General.shufflePercentage -> shufflePercentage
        Rule.General.blackjackPayout -> blackjackPayout
        Rule.General.push22 -> boolean
        Rule.Dealer.hitOnSoft17 -> boolean
        Rule.Dealer.peeks -> boolean
        Rule.Dealer.checksBlackjack -> boolean
        Rule.TrippleSeven.enabled -> boolean
        Rule.TrippleSeven.payout -> payout
        Rule.Split.amount -> splitAmount
        Rule.Split.acesAmount -> splitAmount
        Rule.Split.newCardsAfterAce -> boolean
        Rule.DoubleDown.afterSplit -> boolean
        Rule.DoubleDown.onlyOnSoft -> boolean
        Rule.DoubleDown.rescue -> boolean
        Rule.Surrender.late -> boolean
        Rule.Surrender.early -> boolean
        Rule.Surrender.againstAce -> boolean
        Rule.Surrender.afterSplit -> boolean
        Rule.FiveCards.enabled -> boolean
        Rule.FiveCards.payout -> payout
        Rule.Insurance.enabled -> boolean
        Rule.Insurance.payout -> payout
        Rule.BustSideBet.enabled -> boolean
        Rule.BustSideBet.payout -> payout
        Rule.LuckyLuckySideBet.enabled -> boolean
        Rule.LuckyLuckySideBet.payout19 -> luckyLuckyPayout
        Rule.LuckyLuckySideBet.payout20 -> luckyLuckyPayout
        Rule.LuckyLuckySideBet.payout21 -> luckyLuckyPayout
        Rule.LuckyLuckySideBet.payout21Match -> luckyLuckyPayout
        Rule.LuckyLuckySideBet.payoutSequence -> luckyLuckyPayout
        Rule.LuckyLuckySideBet.payoutTripple -> luckyLuckyPayout
        Rule.LuckyLuckySideBet.payoutSequenceMatch -> luckyLuckyPayout
        Rule.LuckyLuckySideBet.payoutTrippleMatch -> luckyLuckyPayout
        else -> throw IllegalArgumentException("Unknown rule: $rule")
    } as Map<Type, String>

}