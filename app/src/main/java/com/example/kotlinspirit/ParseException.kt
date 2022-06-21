package com.example.kotlinspirit

class ParseException(
    private val result: Long,
    string: CharSequence
) : Exception(
    "Failed to parse token: ${string.subSequence(0, result.getSeek())}, error: ${
        result.getStepCode().stepCodeToString()
    }"
) {
    val errorCode: Int
        get() = result.getStepCode()

    val seek: Int
        get() = result.getSeek()
}