package com.example.kotlinspirit

class OrRule<T>(
    private val a: Rule<T>,
    private val b: Rule<T>
) : Rule<T> {
    private var aFailed = false

    override fun parse(state: ParseState, requireResult: Boolean) {
        aFailed = false
        a.parse(state, false)
        if (state.hasError) {
            aFailed = true
            b.parse(state, false)
        }
    }

    override fun getResult(array: CharArray, seekBegin: Int, seekEnd: Int): T {
        return if (aFailed) {
            b.getResult(array, seekBegin, seekEnd)
        } else {
            a.getResult(array, seekBegin, seekEnd)
        }
    }
}