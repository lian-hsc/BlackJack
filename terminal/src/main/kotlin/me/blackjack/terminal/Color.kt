package me.blackjack.terminal

import me.blackjack.terminal.impl.constant.Ansi

fun String.rgb(r: Int, g: Int, b: Int) =
    "${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.foreground(r, g, b)}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.RESET_FOREGROUND}"

fun String.rgb(hex: String) =
    "${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.foreground(hex)}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.RESET_FOREGROUND}"

fun String.bg(r: Int, g: Int, b: Int) =
    "${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.background(r, g, b)}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.RESET_BACKGROUND}"

fun String.bg(hex: String) =
    "${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.background(hex)}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.RESET_BACKGROUND}"