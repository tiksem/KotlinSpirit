package com.example.kotlinspiritxx

import com.example.kotlinspirit.*
import kotlin.math.min

class RangeStringRule(
    private val range: IntRange,
    private val rule: BaseRule<CharSequence>
) : StringRule() {
    override fun createParseIterator(): ParseIterator<CharSequence> {
        return rule.iterator
    }

    override fun checkPostCondition(string: CharSequence, state: ParseState) {
        if (state.tokenLength < range.first) {
            state.parseCode = StepCode.STRING_TOO_SHORT
            return
        }
    }

    override fun parse(
        state: ParseState,
        string: CharSequence,
        requireResult: Boolean,
        maxLength: Int?
    ): CharSequence? {
        return super.parse(
            state, string, requireResult, min(
                maxLength ?: string.length,
                range.last + state.seek)
        )
    }
}