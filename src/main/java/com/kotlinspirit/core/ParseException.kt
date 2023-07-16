package com.kotlinspirit.core

class ParseException(
    private val result: ParseSeekResult,
    private val string: CharSequence
) : Exception(
    "Failed to parse token: ${string.subSequence(0, result.seek)}, error: ${
        result.parseCodeString
    }"
) {
    val errorCode: Int
        get() = result.errorCode

    val seek: Int
        get() = result.seek

    val token: CharSequence
        get() = string.subSequence(0..seek)

    // Started from 0
    val lineNumber: Int
        get() = token.count { it == '\n' }

    val seekInLine: Int
        get() = seek - token.lastIndexOf('\n')
}