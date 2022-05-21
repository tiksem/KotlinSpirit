package com.example.kotlinspirit

import kotlin.math.min

private const val DOES_NOT_MATCH = "required string was not found"

abstract class StringRule: Rule<String> {
    override fun getResult(array: CharArray, seekBegin: Int, seekEnd: Int): String {
        return String(array, seekBegin, seekEnd - seekBegin)
    }
}

abstract class MatchStringRule(
    private val minimumLength: Int,
    private val maximumLength: Int,
) : StringRule() {
    protected abstract fun isValidChar(char: Char): Boolean
    abstract operator fun get(range: IntRange): MatchStringRule

    override fun parse(state: ParseState, requireResult: Boolean) {
        if (state.seek + minimumLength > state.array.size) {
            state.errorReason = DOES_NOT_MATCH
            return
        }

        state.startParseToken()
        var i = 0
        val max = min(maximumLength, state.array.size - state.seekTokenBegin)
        while (i < max) {
            if (!isValidChar(state.array[i + state.seekTokenBegin])) {
                break
            }

            i++
        }

        state.seek = i + state.seekTokenBegin
        if (i < minimumLength) {
            state.errorReason = DOES_NOT_MATCH
        }
    }
}