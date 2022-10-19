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
        return string.length < seek && string[seek] == char
    }

    override fun clone(): ExactCharRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): ExactCharRule {
        return DebugExactCharRule(name ?: "'$char'", char)
    }
}

private class DebugExactCharRule(override val name: String, char: Char) :
    ExactCharRule(char),
    DebugRule
{
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}