package com.kotlinspirit.char

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugRule

open class ExactCharRule(
    private val char: Char
) : CharPredicateRule(data = CharPredicateData(chars = charArrayOf(char))) {
    override fun parse(seek: Int, string: CharSequence): Long {
        if (seek >= string.length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        return if (string[seek] == char) {
            createStepResult(
                seek = seek + 1,
                parseCode = ParseCode.COMPLETE
            )
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        if (seek >= string.length) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            return
        }

        if (string[seek] == char) {
            result.parseResult = createStepResult(
                seek = seek + 1,
                parseCode = ParseCode.COMPLETE
            )
            result.data = char
        } else {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < string.length && string[seek] == char
    }

    override fun clone(): ExactCharRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "char($char)"
}