package com.example.kotlinspiritxx

import com.example.kotlinspirit.*

private class RangeStringRuleIterator(
    private val range: IntRange,
    private val iterator: ParseIterator<CharSequence>
) : ParseIterator<CharSequence> by iterator {
    override fun next(context: ParseContext): Int {
        val next = iterator.next(context)
        return if (next == StepCode.HAS_NEXT_MAY_COMPLETE) {
            if (iterator.getTokenLength() >= range.first) {
                StepCode.HAS_NEXT_MAY_COMPLETE
            } else {
                StepCode.HAS_NEXT
            }
        } else if (next == StepCode.COMPLETE) {
            if (iterator.getTokenLength() >= range.first) {
                StepCode.COMPLETE
            } else {
                StepCode.STRING_TOO_SHORT
            }
        } else {
            next
        }
    }
}

class RangeStringRule(
    private val range: IntRange,
    private val rule: BaseRule<CharSequence>
) : StringRule() {
    override fun createParseIterator(): ParseIterator<CharSequence> {
        return RangeStringRuleIterator(range, rule.iterator)
    }
}