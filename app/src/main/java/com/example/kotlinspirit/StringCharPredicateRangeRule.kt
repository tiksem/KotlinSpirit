package com.example.kotlinspirit

import java.lang.UnsupportedOperationException
import kotlin.math.min

class StringCharPredicateRangeRule(
    private val predicate: (Char) -> Boolean,
    private val range: IntRange
) : RuleWithDefaultRepeat<CharSequence>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        var i = seek
        val limit = min(string.length, range.last + seek)
        while (i < limit) {
            val c = string[i]
            if (!predicate(c)) {
                return if (i - seek >= range.first) {
                    createComplete(i)
                } else {
                    createStepResult(
                        seek = i,
                        ParseCode.STRING_NOT_ENOUGH_DATA
                    )
                }
            }

            i++
        }

        return if (i - seek >= range.first) {
            createComplete(i)
        } else {
            createStepResult(
                seek = i,
                ParseCode.STRING_NOT_ENOUGH_DATA
            )
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        var i = seek
        val limit = min(string.length, range.last + seek)
        while (i < limit) {
            val c = string[i]
            if (!predicate(c)) {
                if (i - seek >= range.first) {
                    result.data = string.subSequence(seek, i)
                    result.stepResult = createComplete(i)
                } else {
                    result.stepResult = createStepResult(
                        seek = i,
                        ParseCode.STRING_NOT_ENOUGH_DATA
                    )
                }
                return
            }

            i++
        }

        if (i - seek >= range.first) {
            result.data = string.subSequence(seek, i)
            result.stepResult = createComplete(i)
        } else {
            result.stepResult = createStepResult(
                seek = i,
                ParseCode.STRING_NOT_ENOUGH_DATA
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < string.length && predicate(string[seek])
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        throw UnsupportedOperationException()
    }

    override fun not(): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(
            predicate = {
                !predicate(it)
            },
            range = range
        )
    }

    override fun clone(): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(predicate, range)
    }
}