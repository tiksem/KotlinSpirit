package com.example.kotlinspirit

private const val MINIMUM_LENGTH_ERROR = "minimum length is not satisfied"

private data class Token(
    val seekBegin: Int,
    val seekEnd: Int
)

class RepeatRule<T>(
    private val minimumLength: Int,
    private val maximumLength: Int,
    private val rule: Rule<T>,
) : Rule<List<T>> {
    private val results = ArrayList<T>()

    override fun parse(state: ParseState, requireResult: Boolean) {
        if (requireResult) {
            results.clear()
        }

        val seekBegin = state.seek
        var i = 0
        while (i <= maximumLength) {
            rule.parse(state, requireResult)
            if (state.hasError) {
                state.seek = state.seekTokenBegin
                state.errorReason = null
                break
            } else {
                if (requireResult) {
                    val result = rule.getResult(state)
                    results.add(result)
                }
                i++
            }
        }
        state.seekTokenBegin = seekBegin
        if (i < minimumLength) {
            state.errorReason = MINIMUM_LENGTH_ERROR
        }
    }

    override fun getResult(array: CharArray, seekBegin: Int, seekEnd: Int): List<T> {
        return results
    }
}