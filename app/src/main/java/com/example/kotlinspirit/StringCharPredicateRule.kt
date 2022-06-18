package com.example.kotlinspirit

import java.lang.UnsupportedOperationException

class StringCharPredicateRule(
    private val predicate: (Char) -> Boolean
) : BaseRule<CharSequence>() {
    private var stepSeekBegin = -1
    private var result: CharSequence = ""

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

    override fun resetStep() {
        stepSeekBegin = -1
        result = ""
    }

    override fun getStepParserResult(string: CharSequence): CharSequence {
        return result
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        if (seek >= string.length) {
            notifyParseStepComplete(string)
            return createStepResult(
                seek = seek,
                stepCode = StepCode.COMPLETE
            )
        }

        if (stepSeekBegin < 0) {
            stepSeekBegin = seek
        }

        val char = string[seek]
        return if (predicate(char)) {
            createStepResult(
                seek = seek + 1,
                stepCode = StepCode.MAY_COMPLETE
            )
        } else {
            result = string.subSequence(stepSeekBegin, seek)
            notifyParseStepComplete(string)
            createStepResult(
                seek = seek,
                stepCode = StepCode.COMPLETE
            )
        }
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        throw UnsupportedOperationException()
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
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