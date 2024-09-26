package me.blackjack.game.impl.game

sealed interface GameReaction

data class EndGame private constructor(val reason: Reason) : GameReaction {

    enum class Reason {

        DEALER_PEEKED_BLACKJACK,
        DEALER_BLACKJACK,
        DEALER_NO_ACTION,
        DEALER_DONE,
        DEALER_BUST,
        ;

        private val instance by lazy { EndGame(this) }

        operator fun invoke() = instance

    }

}

data object AwaitInput : GameReaction

data object StartDealer: GameReaction

data object GameDone : GameReaction