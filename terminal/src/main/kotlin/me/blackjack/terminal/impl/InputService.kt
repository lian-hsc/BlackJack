package me.blackjack.terminal.impl

import me.blackjack.terminal.*
import me.blackjack.terminal.impl.constant.KeyCodes
import org.koin.core.annotation.Single
import kotlin.concurrent.thread

@Single
internal class InputService(private val jLine: JLineService) {

    private val subscribers = mutableListOf<(Key) -> Unit>()
    private val buffer = mutableListOf<Int>()

    init {
        thread {
            val buffered = jLine.reader.buffered()
            var input = 0

            while (runCatching { input = buffered.read() }.isSuccess) {
                buffer.add(input)

                if (input == KeyCodes.ESC) {
                    Thread.sleep(1)

                    if (buffered.ready()) buffer.add(buffered.read())
                }

                handleInput()
            }
        }
    }

    fun subscribe(callback: (Key) -> Unit) {
        subscribers.add(callback)
    }

    private fun handleInput() {
        val key =
            if (buffer.size > 1) {
                when (buffer) {
                    KeyCodes.ARROW_UP_SEQUENCE -> ArrowKey(ArrowKey.Direction.UP)
                    KeyCodes.ARROW_DOWN_SEQUENCE -> ArrowKey(ArrowKey.Direction.DOWN)
                    KeyCodes.ARROW_RIGHT_SEQUENCE -> ArrowKey(ArrowKey.Direction.RIGHT)
                    KeyCodes.ARROW_LEFT_SEQUENCE -> ArrowKey(ArrowKey.Direction.LEFT)
                    else -> null
                }
            } else if (buffer.first() == KeyCodes.ESC) EscapeKey
            else if (buffer.first() == KeyCodes.CARRIAGE_RETURN) EnterKey
            else if (buffer.first() == KeyCodes.BACKSPACE) BackspaceKey
            else AlphaNumericKey(buffer.first().toChar())

        if (key == null) return

        buffer.clear()
        subscribers.forEach { it(key) }
    }

}