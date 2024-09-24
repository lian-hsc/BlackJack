package me.blackjack.terminal

sealed interface Key

data class AlphaNumericKey(val char: Char) : Key

data class ArrowKey(val direction: Direction) : Key {

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

}

data object EnterKey : Key

data object EscapeKey : Key

data object BackspaceKey : Key