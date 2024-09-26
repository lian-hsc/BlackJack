package me.blackjack.game.impl.game

sealed interface PlayerInput

data class PregameSideBet(val type: Type, val bet: Long) : PlayerInput {

    enum class Type {

        BUST,
        LUCKY_LUCKY
        ;

    }

}

data class SetBet(val bet: Long) : PlayerInput

data object Start : PlayerInput

data class Insure(val bet: Long): PlayerInput

data object Hit : PlayerInput

data object Stand : PlayerInput

data object Double : PlayerInput

data object Split : PlayerInput

data object SurrenderCurrent : PlayerInput

data object Proceed : PlayerInput

data class Surrender(val hands: List<Int>) : PlayerInput

data object FinishGame : PlayerInput

data object AbortGame : PlayerInput