package me.blackjack.terminal.impl.constant

internal object Ansi {

    const val ESCAPE_SEQUENCE = "\u001b["

    object Cursor {

        const val INVISIBLE = "?25l"
        const val VISIBLE = "?25h"

        fun toLine(line: Int) = "${line};1H"

    }

    object Erase {

        const val SCREEN = "2J"
        const val LINE = "2K"

    }

    object Style {

        const val BOLD = "1m"
        const val NO_BOLD = "22m"
        const val ITALIC = "3m"
        const val NO_ITALIC = "23m"
        const val UNDERLINE = "4m"
        const val NO_UNDERLINE = "24m"

    }

    object Color {

        fun foreground(r: Int, g: Int, b: Int) = "38;2;${r};${g};${b}m"
        fun foreground(hex: String) = foreground(
            hex.substring(0, 2).toInt(16),
            hex.substring(2, 4).toInt(16),
            hex.substring(4, 6).toInt(16)
        )
        const val RESET_FOREGROUND = "39m"

        fun background(r: Int, g: Int, b: Int) = "48;2;${r};${g};${b}m"
        const val RESET_BACKGROUND = "49m"

    }

}