package com.example.kotlinspirit

object Command {
    const val MATCH_CHAR = 0
    const val MATCH_EXACT_CHAR = 1
    const val ANY_CHAR = 2
    const val ANY_INT = 3
    const val ANY_UNSIGNED_INT = 4
    const val OR = 5
    const val SEQUENCE = 6
    const val ONE_OR_MORE = 7
    const val ZERO_OR_MORE = 8
    const val REPEAT = 9
    const val RESULT = 10
    const val GRAMMAR = 11
    const val MATCH_CHAR_STR = 12
    const val ANY_CHAR_STR = 13
    const val ONE_OF = 14
}

inline fun IntArray.getPredicate(): Int {
    return this[1]
}

inline fun IntArray.getStrCharMin(): Int {
    return this[2]
}

inline fun IntArray.getStrCharMax(): Int {
    return this[3]
}