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
    const val NO_FAILED = 10
}

fun Int.isError(): Boolean {
    return this > StepCode.COMPLETE
}

fun Int.isErrorOrComplete(): Boolean {
    return this > StepCode.MAY_COMPLETE
}

fun Int.isNextOrMayComplete(): Boolean {
    return this < StepCode.COMPLETE
}

fun Int.canComplete(): Boolean {
    return this == StepCode.MAY_COMPLETE || this == StepCode.COMPLETE
}

fun Long.getSeek(): Int {
    return (this shr 32).toInt()
}

fun Long.getStepCode(): Int {
    return toInt()
}

fun Long.toSeekOrError(): Int {
    val code = getStepCode()
    if (code.isError()) {
        return -code
    } else {
        return getSeek()
    }
}

fun createStepResult(seek: Int, stepCode: Int): Long {
    return seek.toLong() shl 32 or stepCode.toLong() and 0xFFFFFFFFL
}

fun Int.errorCodeToString(): String {
    return when (this) {
        StepCode.EOF -> "EOF"
        StepCode.INVALID_INT -> "INVALID_INT"
        StepCode.INT_STARTED_FROM_ZERO -> "INT_STARTED_FROM_ZERO"
        StepCode.INT_OUT_OF_BOUNDS -> "INT_OUT_OF_BOUNDS"
        StepCode.DIFF_FAILED -> "DIFF_FAILED"
        StepCode.CHAR_PREDICATE_FAILED -> "CHAR_PREDICATE_FAILED"
        StepCode.STRING_NOT_ENOUGH_DATA -> "STRING_NOT_ENOUGH_DATA"
        StepCode.NO_FAILED -> "NO_FAILED"
        else -> "UNKNOWN"
    }
}
