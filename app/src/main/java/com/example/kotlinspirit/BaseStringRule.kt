package com.example.kotlinspirit

import kotlin.math.min

private const val DOES_NOT_MATCH = "required string was not found"

abstract class BaseStringRule(
    private val minimumLength: Int,
    private val maximumLength: Int,
) : Rule<String> {
    protected abstract fun isValidChar(char: Char): Boolean
    abstract operator fun get(range: IntRange): BaseStringRule

    override fun parse(state: ParseState) {
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

    override fun getResult(state: ParseState): String {
        return state.getToken()
    }
}