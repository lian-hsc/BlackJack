package me.blackjack.game.impl.game.sidebet

internal sealed interface SideBet {

    val bet: Long

    val state: State
    val payout: Double

    fun startGame() {}

    enum class State {

        PENDING,
        WON,
        LOST,

    }
}