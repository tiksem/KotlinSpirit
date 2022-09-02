package com.example.kotlinspirit

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
                    result.parseResult = createComplete(i)
                } else {
                    result.parseResult = createStepResult(
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
            result.parseResult = createComplete(i)
        } else {
            result.parseResult = createStepResult(
                seek = i,
                ParseCode.STRING_NOT_ENOUGH_DATA
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < string.length && predicate(string[seek])
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        var i = seek
        val length = string.length
        while (i < length) {
            val c = string[i]
            if (predicate(c)) {
                break
            }

            i++
        }

        var j = i
        do {
            val c = string[j]
            if (!predicate(c)) {
                break
            }

            j++
        }  while(j < length)

        return if (j - i >= range.first) {
            if (i == seek) {
                -i - 1
            } else {
                i
            }
        } else {
            noParse(j + 1, string).let {
                if (it < 0) {
                    j + 1
                } else {
                    it
                }
            }
        }
    }

    override fun clone(): StringCharPredicateRangeRule {
        return this
    }
}