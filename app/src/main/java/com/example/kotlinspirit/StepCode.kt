package com.example.kotlinspirit

object StepCode {
    const val HAS_NEXT = 0
    const val MAY_COMPLETE = 1
    const val COMPLETE = 2
    const val EOF = 3
    const val INVALID_INT = 4
    const val INT_STARTED_FROM_ZERO = 5
    const val INT_OUT_OF_BOUNDS = 6
    const val DIFF_FAILED = 7
    const val CHAR_PREDICATE_FAILED = 8
    const val STRING_NOT_ENOUGH_DATA = 9
    const val STRING_DOES_NOT_MATCH = 10
    const val NO_FAILED = 11
    const val INVALID_DOUBLE = 12
}

inline fun Int.isError(): Boolean {
    return this > StepCode.COMPLETE
}

inline fun Int.isNotError(): Boolean {
    return this <= StepCode.COMPLETE
}

inline fun Int.isErrorOrComplete(): Boolean {
    return this > StepCode.MAY_COMPLETE
}

inline fun Int.isNextOrMayComplete(): Boolean {
    return this < StepCode.COMPLETE
}

inline fun Int.canComplete(): Boolean {
    return this == StepCode.MAY_COMPLETE || this == StepCode.COMPLETE
}

inline fun Long.getSeek(): Int {
    return (this shr 32).toInt()
}

inline fun Long.getStepCode(): Int {
    return toInt()
}

inline fun createStepResult(seek: Int, stepCode: Int): Long {
    return seek.toLong() shl 32 or (stepCode.toLong() and 0xFFFFFFFFL)
}

inline fun createComplete(seek: Int): Long {
    return createStepResult(seek, StepCode.COMPLETE)
}

fun Long.stepResultToString(): String {
    return "seek = ${getSeek()}, code = ${getStepCode().stepCodeToString()}"
}

inline fun Int.stepCodeToString(): String {
    return when (this) {
        StepCode.COMPLETE -> "COMPLETE"
        StepCode.HAS_NEXT -> "HAS_NEXT"
        StepCode.MAY_COMPLETE -> "MAY_COMPLETE"
        StepCode.EOF -> "EOF"
        StepCode.INVALID_INT -> "INVALID_INT"
        StepCode.INT_STARTED_FROM_ZERO -> "INT_STARTED_FROM_ZERO"
        StepCode.INT_OUT_OF_BOUNDS -> "INT_OUT_OF_BOUNDS"
        StepCode.DIFF_FAILED -> "DIFF_FAILED"
        StepCode.CHAR_PREDICATE_FAILED -> "CHAR_PREDICATE_FAILED"
        StepCode.STRING_NOT_ENOUGH_DATA -> "STRING_NOT_ENOUGH_DATA"
        StepCode.STRING_DOES_NOT_MATCH -> "STRING_NOT_ENOUGH_DATA"
        StepCode.NO_FAILED -> "NO_FAILED"
        StepCode.INVALID_DOUBLE -> "INVALID_DOUBLE"
        else -> "UNKNOWN"
    }
}
