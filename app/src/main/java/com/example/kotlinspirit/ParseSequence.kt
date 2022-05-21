package com.example.kotlinspirit

internal fun String.toParseState(): ParseState {
    return ParseState(toCharArray())
}