package com.example.kotlinspirit

import java.lang.Exception

class ParseState(
    val array: CharArray
) {
    internal var seekTokenBegin: Int = -1
    internal var seek: Int = 0

    var errorReason: String? = null
        internal set

    val hasError: Boolean
        get() = errorReason != null

    companion object {
        const val EOF = "eof"
    }

    fun readChar(): Char {
        return array[seek++]
    }

    fun getChar(): Char {
        return array[seek]
    }

    fun startParseToken() {
        seekTokenBegin = seek
    }

    fun checkEof(): Boolean {
        if (isEof()) {
            errorReason = EOF
            return true
        }

        return false
    }

    fun isEof(): Boolean {
        return seek >= array.size
    }

    fun getToken(): String {
        return String(
            array, seekTokenBegin,
            seek - seekTokenBegin
        )
    }
}

class ParseException(
    state: ParseState,
    tokenName: String
) : Exception("Failed to parse $tokenName in range: [${state.seekTokenBegin}, ${state.seek}), " +
        "token: ${state.getToken()}, " +
        "reason: ${state.errorReason}")