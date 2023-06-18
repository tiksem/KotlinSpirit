package com.kotlinspirit.core

object ParseCode {
    const val COMPLETE = 0
    const val EOF = 1
    const val INVALID_INT = 2
    const val INVALID_UINT = 3
    const val INVALID_LONG = 4
    const val INVALID_ULONG = 5
    const val INVALID_SHORT = 6
    const val INVALID_USHORT = 7
    const val INVALID_BYTE = 8
    const val INVALID_UBYTE = 9
    const val INT_OUT_OF_BOUNDS = 10
    const val UINT_OUT_OF_BOUNDS = 11
    const val SHORT_OUT_OF_BOUNDS = 12
    const val USHORT_OUT_OF_BOUNDS = 13
    const val LONG_OUT_OF_BOUNDS = 14
    const val ULONG_OUT_OF_BOUNDS = 15
    const val BYTE_OUT_OF_BOUNDS = 16
    const val UBYTE_OUT_OF_BOUNDS = 17
    const val DIFF_FAILED = 18
    const val CHAR_PREDICATE_FAILED = 19
    const val STRING_NOT_ENOUGH_DATA = 20
    const val STRING_DOES_NOT_MATCH = 21
    const val NO_FAILED = 22
    const val INVALID_DOUBLE = 23
    const val INVALID_FLOAT = 24
    const val WHOLE_STRING_DOES_NOT_MATCH = 25
    const val ONE_OF_STRING_NOT_FOUND = 26
    const val FAIL_PREDICATE = 27
    const val SUFFIX_EXPECTATION_FAILED = 28
    const val SPLIT_NOT_ENOUGH_DATA = 29
    const val INVALID_BIG_INTEGER = 30
    const val INVALID_BIG_DECIMAL = 31
    const val BIG_DECIMAL_EXPONENT_OVERFLOW = 32
    const val NO_EOF = 33
    const val PREFIX_NOT_SATISFIED = 3000
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

internal fun Int.parseCodeToString(): String {
    return when (this) {
        ParseCode.COMPLETE -> "COMPLETE"
        ParseCode.EOF -> "EOF"
        ParseCode.INVALID_INT -> "INVALID_INT"
        ParseCode.INVALID_UINT -> "INVALID_UINT"
        ParseCode.INVALID_LONG -> "INVALID_LONG"
        ParseCode.INVALID_ULONG -> "INVALID_ULONG"
        ParseCode.INT_OUT_OF_BOUNDS -> "INT_OUT_OF_BOUNDS"
        ParseCode.LONG_OUT_OF_BOUNDS -> "LONG_OUT_OF_BOUNDS"
        ParseCode.UINT_OUT_OF_BOUNDS -> "UINT_OUT_OF_BOUNDS"
        ParseCode.ULONG_OUT_OF_BOUNDS -> "ULONG_OUT_OF_BOUNDS"
        ParseCode.INVALID_BYTE -> "INVALID_BYTE"
        ParseCode.INVALID_UBYTE -> "INVALID_UBYTE"
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
        ParseCode.SUFFIX_EXPECTATION_FAILED -> "SUFFIX_EXPECTATION_FAILED"
        ParseCode.INVALID_BIG_INTEGER -> "INVALID_BIG_INTEGER"
        ParseCode.INVALID_BIG_DECIMAL -> "INVALID_BIG_DECIMAL"
        ParseCode.BIG_DECIMAL_EXPONENT_OVERFLOW -> "BIG_DECIMAL_EXPONENT_OVERFLOW"
        ParseCode.BYTE_OUT_OF_BOUNDS -> "BYTE_OUT_OF_BOUNDS"
        ParseCode.UBYTE_OUT_OF_BOUNDS -> "UBYTE_OUT_OF_BOUNDS"
        ParseCode.NO_EOF -> "NO_EOF"
        else -> if (this > ParseCode.PREFIX_NOT_SATISFIED) {
            "PREFIX_NOT_SATISFIED_" + (this - ParseCode.PREFIX_NOT_SATISFIED).parseCodeToString()
        } else {
            "UNKNOWN_ERROR"
        }
    }
}

internal fun Long.limitPrefixMaxMatchLength(): Int {
    return coerceAtMost(Rule.MAX_PREFIX_LENGTH.toLong()).toInt()
}

internal fun Int.limitPrefixMaxMatchLength(): Int {
    return coerceAtMost(Rule.MAX_PREFIX_LENGTH)
}
