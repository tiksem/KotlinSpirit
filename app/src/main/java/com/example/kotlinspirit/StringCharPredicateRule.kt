package com.example.kotlinspirit

import java.lang.UnsupportedOperationException

class StringCharPredicateRule(
    private val predicate: (Char) -> Boolean
) : RuleWithDefaultRepeat<CharSequence>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        var i = seek
        while (i < string.length) {
            val c = string[i]
            if (!predicate(c)) {
                return createComplete(i)
            }

            i++
        }

        return createComplete(i)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        var i = seek
        while (i < string.length) {
            val c = string[i]
            if (!predicate(c)) {
                result.data = string.subSequence(seek, i)
                result.stepResult = createComplete(i)
                return
            }

            i++
        }

        result.data = string.subSequence(seek, i)
        result.stepResult = createComplete(i)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        throw UnsupportedOperationException()
    }

    override fun not(): StringCharPredicateRule {
        return StringCharPredicateRule(
            predicate = {
                !predicate(it)
            }
        )
    }

    override fun clone(): StringCharPredicateRule {
        return StringCharPredicateRule(predicate)
    }
}