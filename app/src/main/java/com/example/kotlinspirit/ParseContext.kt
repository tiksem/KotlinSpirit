package com.example.kotlinspirit

class ParseContext(
    val string: CharSequence,
    val skipper: Rule<*>? = null
)