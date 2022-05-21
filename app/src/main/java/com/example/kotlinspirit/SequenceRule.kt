package com.example.kotlinspirit

class SequenceRule(
    private val a: Rule<*>,
    private val b: Rule<*>
) : StringRule() {
    private var seekBegin = -1

    override fun parse(state: ParseState, requireResult: Boolean) {
        seekBegin = state.seek
        a.parse(state, false)
        if (!state.hasError) {
            b.parse(state, false)
        }
    }
}