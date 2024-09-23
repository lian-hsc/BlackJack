package me.blackjack.terminal

import me.blackjack.terminal.impl.Ansi

fun String.bold() = "${Ansi.ESCAPE_SEQUENCE}${Ansi.Style.BOLD}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Style.NO_BOLD}"
fun String.italic() = "${Ansi.ESCAPE_SEQUENCE}${Ansi.Style.ITALIC}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Style.NO_ITALIC}"
fun String.underline() = "${Ansi.ESCAPE_SEQUENCE}${Ansi.Style.UNDERLINE}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Style.NO_UNDERLINE}"