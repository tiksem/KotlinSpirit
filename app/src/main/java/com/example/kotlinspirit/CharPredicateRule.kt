package com.example.kotlinspirit

import java.lang.UnsupportedOperationException

class CharPredicateRule(
    private val predicate: (Char) -> Boolean
) : Rule<Char> {
    private var result = 0.toChar()

    override fun parse(seek: Int, string: CharSequence): Int {
        if (seek >= string.length) {
            return -StepCode.EOF
        }

        return if (predicate(string[seek])) {
            seek + 1
        } else {
            -StepCode.CHAR_PREDICATE_FAILED
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        if (seek >= string.length) {
            result.errorCodeOrSeek = -StepCode.EOF
            return
        }

        val ch = string[seek]
        if (predicate(ch)) {
            result.data = ch
            result.errorCodeOrSeek = seek + 1
        } else {
            result.errorCodeOrSeek = -StepCode.CHAR_PREDICATE_FAILED
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < string.length && predicate(string[seek])
    }

    override fun resetStep() {
    }

    override fun getStepParserResult(string: CharSequence): Char {
        return result
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        if (seek >= string.length) {
            return createStepResult(
                seek = seek,
                stepCode = StepCode.EOF
            )
        }

        val char = string[seek]
        return if (predicate(char)) {
            result = char
            notifyParseStepComplete(string)
            createStepResult(
                seek = seek + 1,
                stepCode = StepCode.COMPLETE
            )
        } else {
            createStepResult(
                seek = seek,
                stepCode = StepCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun clone(): CharPredicateRule {
        return CharPredicateRule(
            predicate = predicate
        )
    }

    override fun not(): CharPredicateRule {
        return CharPredicateRule(
            predicate = {
                !predicate(it)
            }
        )
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        throw UnsupportedOperationException()
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        throw UnsupportedOperationException()
    }
}