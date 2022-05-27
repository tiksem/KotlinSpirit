package com.example.kotlinspirit

import java.lang.Exception

class ParseState(
    var beginSeek: Int = 0,
    var seek: Int = 0,
    var parseCode: Int = StepCode.HAS_NEXT
) {
    fun startParseToken() {
        beginSeek = seek
    }

    val errorDescription: String get() = parseCode.getErrorDescription()
    val hasError: Boolean get() = parseCode.isError()
    val tokenLength: Int get() = seek - beginSeek
}

class ParseException(
    string: CharSequence,
    state: ParseState,
    tokenName: String
) : Exception("Failed to parse $tokenName in range: [${state.beginSeek}, ${state.seek}), " +
        "token: ${string.subSequence(state.beginSeek, state.seek)}, " +
        "reason: ${state.errorDescription}")