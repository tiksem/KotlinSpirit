package com.example.kotlinspirit

private const val MATCH_NOT_FOUND = "exact match not found"

internal class ExactStringRule(
    private val string: String
) : Rule<String> {
    override fun parse(state: ParseState, requireResult: Boolean) {
        state.startParseToken()
        if (state.seek + string.length > state.array.size) {
            state.seek = state.array.size
            state.errorReason = ParseState.EOF
            return
        }

        string.forEach {
            if (state.readChar() != it) {
                state.errorReason = MATCH_NOT_FOUND
            }
        }
    }

    override fun getResult(array: CharArray, seekBegin: Int, seekEnd: Int): String {
        return string
    }
}

fun str(string: String): Rule<String> {
    return ExactStringRule(string)
}