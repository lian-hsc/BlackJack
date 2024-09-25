package me.blackjack.terminal

import me.blackjack.terminal.impl.constant.Ansi

fun String.rgb(r: Int, g: Int, b: Int) =
    "${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.foreground(r, g, b)}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.RESET_FOREGROUND}"

fun String.bgRgb(r: Int, g: Int, b: Int) =
    "${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.background(r, g, b)}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.RESET_BACKGROUND}"