package com.example.kotlinspirit

private const val DIFFERENCE_MATCH = "difference match"

class DifferenceRule<T>(
    private val main: Rule<T>,
    private val exception: Rule<*>
) : Rule<T> {
    override fun parse(state: ParseState, requireResult: Boolean) {
        main.parse(state, requireResult)
        if (state.hasError) {
            return
        }

        exception.parse(state)
        if (state.hasError) {
            state.errorReason = null
            state.seek = state.seekTokenBegin
        } else {
            state.errorReason = DIFFERENCE_MATCH
        }
    }

    override fun getResult(array: CharArray, seekBegin: Int, seekEnd: Int): T {
        return main.getResult(array, seekBegin, seekEnd)
    }
}