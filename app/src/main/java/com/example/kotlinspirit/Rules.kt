package com.example.kotlinspirit

object Rules {
    val int = IntRule(commands = intArrayOf(Command.ANY_INT))
    val uint = IntRule(commands = intArrayOf(Command.ANY_UNSIGNED_INT))
}