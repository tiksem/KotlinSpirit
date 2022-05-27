package com.example.kotlinspirit

import com.example.kotlinspiritxx.RangeStringRule

abstract class StringRule: BaseRule<CharSequence>() {
    operator fun get(range: IntRange): RangeStringRule {
        return RangeStringRule(range, this)
    }
}

private class MatchStringRuleIterator(
    private val predicate: (Char) -> Boolean
) : BaseStringIterator() {
    override fun next(): Int {
        if (isEof()) {
            return StepCode.COMPLETE
        }

        val char = getChar()
        return if (!predicate(char)) {
            StepCode.COMPLETE
        } else {
            seek++
            StepCode.HAS_NEXT_MAY_COMPLETE
        }
    }
}

private class MatchStringRule(
    private val predicate: (Char) -> Boolean
) : StringRule() {
    override fun createParseIterator(): ParseIterator<CharSequence> {
        return MatchStringRuleIterator(predicate)
    }
}

fun str(vararg chars: Char): StringRule {
    assert(chars.isNotEmpty())
    return MatchStringRule(CharPredicates.from(*chars))
}

fun str(vararg ranges: CharRange): StringRule {
    assert(ranges.isNotEmpty())
    return MatchStringRule(CharPredicates.from(*ranges))
}

fun str(
    ranges: Array<CharRange>,
    chars: CharArray
) : StringRule {
    assert(ranges.isNotEmpty() || chars.isNotEmpty())
    return MatchStringRule(
        CharPredicates.from(ranges, chars)
    )
}

fun str(predicate: (Char) -> Boolean) : StringRule {
    return MatchStringRule(predicate)
}