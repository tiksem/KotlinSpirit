package com.kotlinspirit.core

object ParseCode {
    const val COMPLETE = 0
    const val EOF = 1
    const val INVALID_INT = 2
    const val INVALID_LONG = 3
    const val INT_STARTED_FROM_ZERO = 4
    const val INT_OUT_OF_BOUNDS = 5
    const val LONG_OUT_OF_BOUNDS = 6
    const val DIFF_FAILED = 7
    const val CHAR_PREDICATE_FAILED = 8
    const val STRING_NOT_ENOUGH_DATA = 9
    const val STRING_DOES_NOT_MATCH = 10
    const val NO_FAILED = 11
    const val INVALID_DOUBLE = 12
    const val INVALID_FLOAT = 13
    const val WHOLE_STRING_DOES_NOT_MATCH = 14
    const val ONE_OF_STRING_NOT_FOUND = 15
    const val FAIL_PREDICATE = 16
    const val EXPECTATION_FAILED = 17
    const val SPLIT_NOT_ENOUGH_DATA = 18
    const val AFTER_FAILED = 19
}

internal inline fun Int.isError(): Boolean {
    return this != ParseCode.COMPLETE
}

internal inline fun Int.isNotError(): Boolean {
    return this == ParseCode.COMPLETE
}

internal inline fun Long.getSeek(): Int {
    return (this shr 32).toInt()
}

internal inline fun Long.getParseCode(): Int {
    return toInt()
}

internal inline fun createStepResult(seek: Int, parseCode: Int): Long {
    return seek.toLong() shl 32 or (parseCode.toLong() and 0xFFFFFFFFL)
}

internal inline fun createComplete(seek: Int): Long {
    return createStepResult(
        seek,
        ParseCode.COMPLETE
    )
}

internal fun Long.stepResultToString(): String {
    return "seek = ${getSeek()}, code = ${getParseCode().parseCodeToString()}"
}

internal inline fun Int.parseCodeToString(): String {
    return when (this) {
        ParseCode.COMPLETE -> "COMPLETE"
        ParseCode.EOF -> "EOF"
        ParseCode.INVALID_INT -> "INVALID_INT"
        ParseCode.INVALID_LONG -> "INVALID_LONG"
        ParseCode.INT_STARTED_FROM_ZERO -> "INT_STARTED_FROM_ZERO"
        ParseCode.INT_OUT_OF_BOUNDS -> "INT_OUT_OF_BOUNDS"
        ParseCode.LONG_OUT_OF_BOUNDS -> "LONG_OUT_OF_BOUNDS"
        ParseCode.DIFF_FAILED -> "DIFF_FAILED"
        ParseCode.CHAR_PREDICATE_FAILED -> "CHAR_PREDICATE_FAILED"
        ParseCode.STRING_NOT_ENOUGH_DATA -> "STRING_NOT_ENOUGH_DATA"
        ParseCode.STRING_DOES_NOT_MATCH -> "STRING_NOT_ENOUGH_DATA"
        ParseCode.NO_FAILED -> "NO_FAILED"
        ParseCode.INVALID_DOUBLE -> "INVALID_DOUBLE"
        ParseCode.INVALID_FLOAT -> "INVALID_FLOAT"
        ParseCode.WHOLE_STRING_DOES_NOT_MATCH -> "WHOLE_STRING_DOES_NOT_MATCH"
        ParseCode.ONE_OF_STRING_NOT_FOUND -> "ONE_OF_STRING_NOT_FOUND"
        ParseCode.FAIL_PREDICATE -> "FAIL_PREDICATE"
        ParseCode.EXPECTATION_FAILED -> "EXPECTATION_FAILED"
        else -> "UNKNOWN"
    }
}