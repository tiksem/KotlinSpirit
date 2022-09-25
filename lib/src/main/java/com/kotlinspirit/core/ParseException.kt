package com.kotlinspirit.core

import com.kotlinspirit.debug.RuleDebugTreeNode

class ParseException(
    private val result: Long,
    private val string: CharSequence,
    val debugTree: RuleDebugTreeNode?
) : Exception(
    "Failed to parse token: ${string.subSequence(0, result.getSeek())}, error: ${
        result.getParseCode().parseCodeToString()
    }"
) {
    val errorCode: Int
        get() = result.getParseCode()

    val seek: Int
        get() = result.getSeek()

    val token: CharSequence
        get() = string.subSequence(0..seek)

    // Started from 0
    val lineNumber: Int
        get() = token.count { it == '\n' }

    val seekInLine: Int
        get() = seek - token.lastIndexOf('\n')
}