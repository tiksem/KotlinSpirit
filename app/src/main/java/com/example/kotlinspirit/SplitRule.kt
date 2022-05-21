package com.example.kotlinspirit

private const val MINIMUM_LENGTH_ERROR = "split minimum length error"

class SplitRule<T>(
    private val minimumLength: Int,
    private val maximumLength: Int,
    private val tokenRule: Rule<T>,
    private val dividerRule: Rule<*>
) : Rule<List<T>> {
    private val results = ArrayList<T>()

    override fun parse(state: ParseState, requireResult: Boolean) {
        if (requireResult) {
            results.clear()
        }

        var i = 0
        val seekBegin = state.seek
        while (i <= maximumLength) {
            tokenRule.parse(state, requireResult)
            if (state.hasError) {
                if (i == 0) {
                    if (minimumLength > 0) {
                        state.errorReason = MINIMUM_LENGTH_ERROR
                    }
                }
                return
            } else if (requireResult) {
                val result = tokenRule.getResult(state)
                results.add(result)
            }
            i++
            dividerRule.parse(state)
            if (state.hasError) {
                if (i < minimumLength) {
                    state.errorReason = MINIMUM_LENGTH_ERROR
                    state.seekTokenBegin = seekBegin
                } else {
                    state.seek = state.seekTokenBegin
                    state.errorReason = null
                }
                return
            }
        }
    }

    override fun getResult(array: CharArray, seekBegin: Int, seekEnd: Int): List<T> {
        return results
    }
}