package com.example.kotlinspirit

object StepCode {
    const val HAS_NEXT = 0
    const val HAS_NEXT_MAY_COMPLETE = 1
    const val COMPLETE = 2
    const val EOF = 3
    const val INVALID_NUMBER = 4
    const val NUMBER_STARTED_FROM_ZERO = 5
    const val INT_NOT_IN_REQUESTED_RANGE = 6
    const val STRING_DOES_NOT_MATCH = 7
    const val STRING_TOO_SHORT = 8
    const val CHAR_DOES_NOT_MATCH = 9
    const val ONE_OF_STRING_NOT_FOUND = 10
    const val NO_PREDICATE_FAILED = 11
    const val DIFFERENCE_PREDICATE_FAILED = 12
    const val SPLIT_NOT_ENOUGH_DATA = 13
    const val REPEAT_NOT_ENOUGH_DATA = 14
}

fun Int.getErrorDescription(): String {
    return when (this) {
        StepCode.EOF -> "eof"
        StepCode.INVALID_NUMBER -> "invalid number"
        StepCode.NUMBER_STARTED_FROM_ZERO -> "number started with zero"
        StepCode.INT_NOT_IN_REQUESTED_RANGE -> "int is not in requested range"
        StepCode.STRING_DOES_NOT_MATCH -> "string does not match"
        StepCode.STRING_TOO_SHORT -> "string too short"
        StepCode.CHAR_DOES_NOT_MATCH -> "char does not match"
        StepCode.ONE_OF_STRING_NOT_FOUND -> "one of string not found"
        StepCode.NO_PREDICATE_FAILED -> "no predicate failed"
        StepCode.DIFFERENCE_PREDICATE_FAILED -> "difference predicate failed"
        StepCode.SPLIT_NOT_ENOUGH_DATA -> "split not enough data"
        StepCode.REPEAT_NOT_ENOUGH_DATA -> "repeat not enough data"
        else -> "unknown"
    }
}

fun Int.isError(): Boolean {
    return this > StepCode.COMPLETE
}

fun Int.canComplete(): Boolean {
    return this == StepCode.HAS_NEXT_MAY_COMPLETE || this == StepCode.COMPLETE
}

fun Int.hasNext(): Boolean {
    return this < StepCode.COMPLETE
}