package com.example.kotlinspirit

class ParseException(
    val errorCode: Int,
) : Exception(
    "Failed to parse token, error: ${errorCode.errorCodeToString()}"
)