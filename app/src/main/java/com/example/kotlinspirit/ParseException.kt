package com.example.kotlinspirit

class ParseException(
    result: Long,
    string: CharSequence
) : Exception(
    "Failed to parse token: ${string.subSequence(0, result.getSeek())}, error: ${
        result.getStepCode().errorCodeToString()
    }"
)