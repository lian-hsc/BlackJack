package me.blackjack.rule.impl.menu

import me.blackjack.rule.Rule

internal object Constants {

    val names = mapOf(
        Rule.General.decks to "How many decks are used?",
        Rule.General.shufflePercentage to "How much of the deck needs to be used before shuffling?",
        Rule.General.blackjackPayout to "How much is the payout for blackjack?",
        Rule.General.push22 to "Should a bust from the dealer count as a push? (Push 22)",
        Rule.Dealer.hitOnSoft17 to "Must the dealer hit on soft 17?",
        Rule.Dealer.peeks to "Should the dealer get his second card with everyone else? (Peek)",
        Rule.Dealer.checksBlackjack to "Should the dealer check for a blackjack at the start?",
        Rule.TrippleSeven.enabled to "Should we play with the Tripple-7-Charlie rule?",
        Rule.TrippleSeven.payout to "How much should the payout be for a Tripple-7-Charlie?",
        Rule.Split.amount to "How often can you split?",
        Rule.Split.acesAmount to "How often can you split aces?",
        Rule.Split.newCardsAfterAce to "Should you get new cards after splitting aces?",
        Rule.DoubleDown.afterSplit to "Can you double after splitting?",
        Rule.DoubleDown.onlyOnSoft to "Can you only double on soft hands (9, 10, 11)?",
        Rule.DoubleDown.rescue to "Can you rescue (surrender) after doubling?",
        Rule.Surrender.late to "Can you surrender after the dealer checked for blackjack?",
        Rule.Surrender.early to "Can you surrender before the dealer checked for blackjack?",
        Rule.Surrender.againstAce to "Can you surrender against an ace from the dealer?",
        Rule.Surrender.afterSplit to "Can you surrender after splitting?",
        Rule.FiveCards.enabled to "Should we play with the Five-Card-Charlie rule (automatic win with 5 cards)?",
        Rule.FiveCards.payout to "How much should the payout be for a Five-Card-Charlie?",
        Rule.Insurance.enabled to "Should we play with insurance against a dealer blackjack?",
        Rule.Insurance.payout to "How much should the payout be for insurance?",
        Rule.BustSideBet.enabled to "Should we play with the Bust-Side-Bet (bet on the dealer busting)?",
        Rule.BustSideBet.payout to "How much should the payout be for the Bust-Side-Bet?",
        Rule.LuckyLuckySideBet.enabled to "Should we play with the Lucky-Lucky-Side-Bet (count the first two cards and the dealer's first card)?",
        Rule.LuckyLuckySideBet.payout19 to "How much should the payout be for a Lucky-Lucky-Hand with 19?",
        Rule.LuckyLuckySideBet.payout20 to "How much should the payout be for a Lucky-Lucky-Hand with 20?",
        Rule.LuckyLuckySideBet.payout21 to "How much should the payout be for a Lucky-Lucky-Hand with 21?",
        Rule.LuckyLuckySideBet.payout21Match to "How much should the payout be for a Lucky-Lucky-Hand with 21 that has the same color?",
        Rule.LuckyLuckySideBet.payoutSequence to "How much should the payout be for a Lucky-Lucky-Hand with a sequence (6, 7, 8)?",
        Rule.LuckyLuckySideBet.payoutTripple to "How much should the payout be for a Lucky-Lucky-Hand with three sevens?",
        Rule.LuckyLuckySideBet.payoutSequenceMatch to "How much should the payout be for a Lucky-Lucky-Hand with a sequence (6, 7, 8) that has the same color?",
        Rule.LuckyLuckySideBet.payoutTrippleMatch to "How much should the payout be for a Lucky-Lucky-Hand with three sevens that have the same color?",
    )

}