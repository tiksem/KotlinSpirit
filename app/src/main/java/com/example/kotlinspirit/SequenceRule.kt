package com.example.kotlinspirit

class SequenceRule(
    private val a: Rule<*>,
    private val b: Rule<*>
) : Rule<String> {
    private var seekBegin = -1

    override fun parse(state: ParseState) {
        seekBegin = state.seek
        a.parse(state)
        if (!state.hasError) {
            b.parse(state)
        }
    }

    override fun getResult(state: ParseState): String {
        assert(seekBegin >= 0)
        return String(state.array, seekBegin, state.seek)
    }
}