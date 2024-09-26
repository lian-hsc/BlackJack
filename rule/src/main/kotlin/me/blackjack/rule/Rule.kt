package me.blackjack.rule

class Rule<Type> private constructor() {

    object General {
        val decks = Rule<Int>()
        val shufflePercentage = Rule<Double>()
        val blackjackPayout = Rule<Double>()
        val push22 = Rule<Boolean>()
    }

    object Dealer {
        val hitOnSoft17 = Rule<Boolean>()
        val peeks = Rule<Boolean>()
        val checksBlackjack = Rule<Boolean>()
    }

    object TrippleSeven {
        val enabled = Rule<Boolean>()
        val payout = Rule<Double?>()
    }

    object Split {
        val amount = Rule<Int?>()
        val acesAmount = Rule<Int?>()
        val newCardsAfterAce = Rule<Boolean>()
    }

    object DoubleDown {
        val afterSplit = Rule<Boolean>()
        val onlyOnSoft = Rule<Boolean>()
        val rescue = Rule<Boolean>()
    }

    object Surrender {
        val late = Rule<Boolean>()
        val early = Rule<Boolean>()
        val againstAce = Rule<Boolean>()
        val afterSplit = Rule<Boolean>()
    }

    object FiveCards {
        val enabled = Rule<Boolean>()
        val payout = Rule<Double?>()
    }

    object Insurance {
        val enabled = Rule<Boolean>()
        val payout = Rule<Double?>()
    }

    object BustSideBet {
        val enabled = Rule<Boolean>()
        val payout = Rule<Double?>()
    }

    object LuckyLuckySideBet {
        val enabled = Rule<Boolean>()

        val payout19 = Rule<Double?>()
        val payout20 = Rule<Double?>()
        val payout21 = Rule<Double?>()

        val payout21Match = Rule<Double?>()

        val payoutSequence = Rule<Double?>()
        val payoutTripple = Rule<Double?>()

        val payoutSequenceMatch = Rule<Double?>()
        val payoutTrippleMatch = Rule<Double?>()
    }

}