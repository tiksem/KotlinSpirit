package com.example.kotlinspirit

class OrRule<T>(
    private val a: Rule<T>,
    private val b: Rule<T>
) : Rule<T> {
    private var aFailed = false

    override fun parse(state: ParseState) {
        aFailed = false
        a.parse(state)
        if (state.hasError) {
            aFailed = true
            b.parse(state)
        }
    }

    override fun getResult(state: ParseState): T {
        return if (aFailed) {
            b.getResult(state)
        } else {
            a.getResult(state)
        }
    }
}