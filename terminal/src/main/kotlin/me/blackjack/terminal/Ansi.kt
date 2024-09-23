package me.blackjack.terminal

internal object Ansi {

    const val ESCAPE_SEQUENCE = "\u001b["

    object Escape {

        const val ERASE_SCREEN = "2J"

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
        const val RESET_FOREGROUND = "39m"

        fun background(r: Int, g: Int, b: Int) = "48;2;${r};${g};${b}m"
        const val RESET_BACKGROUND = "49m"

    }

}