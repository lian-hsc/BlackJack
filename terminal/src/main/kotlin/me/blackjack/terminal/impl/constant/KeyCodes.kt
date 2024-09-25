package me.blackjack.terminal.impl.constant

internal object KeyCodes {

    const val ESC = 27

    private const val ARROW_DESIGNATOR = 79
    private const val ARROW_UP = 65
    private const val ARROW_DOWN = 66
    private const val ARROW_RIGHT = 67
    private const val ARROW_LEFT = 68

    val ARROW_UP_SEQUENCE = listOf(ESC, ARROW_DESIGNATOR, ARROW_UP)
    val ARROW_DOWN_SEQUENCE = listOf(ESC, ARROW_DESIGNATOR, ARROW_DOWN)
    val ARROW_RIGHT_SEQUENCE = listOf(ESC, ARROW_DESIGNATOR, ARROW_RIGHT)
    val ARROW_LEFT_SEQUENCE = listOf(ESC, ARROW_DESIGNATOR, ARROW_LEFT)

    const val CARRIAGE_RETURN = 13
    const val BACKSPACE = 127

}