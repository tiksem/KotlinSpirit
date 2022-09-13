package com.example.kotlinspirit

class ParseException(
    private val result: Long,
    string: CharSequence,
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
}