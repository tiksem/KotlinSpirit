package com.example.kotlinspirit

import java.lang.Exception

class ParseException(
    val string: CharSequence,
    val seek: Int,
    val errorCode: Int
) : Exception("Failed to parse in $seek, " +
        "token: ${string.subSequence(0, seek)}, " +
        "reason: ${errorCode.getErrorDescription()}")