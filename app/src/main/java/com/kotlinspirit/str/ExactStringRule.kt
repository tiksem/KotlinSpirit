package com.kotlinspirit.str

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.str.oneof.OneOfStringRule

open class ExactStringRule(
    internal val string: String
) : RuleWithDefaultRepeat<CharSequence>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        val str = string.subSequence(seek, string.length)
        return if (str.startsWith(this.string)) {
            createComplete(
                seek = seek + this.string.length
            )
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.STRING_DOES_NOT_MATCH
            )
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<CharSequence>
    ) {
        val str = string.subSequence(seek, string.length)
        if (str.startsWith(this.string)) {
            result.parseResult = createComplete(
                seek = seek + this.string.length
            )
            result.data = string.subSequence(seek, this.string.length + seek)
        } else {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.STRING_DOES_NOT_MATCH
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return string.subSequence(seek, string.length).startsWith(this.string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        val findIndex = string.indexOf(this.string, seek)
        return if (findIndex == seek) {
            -seek - 1
        } else {
            if (findIndex < 0) {
                string.length
            } else {
                findIndex
            }
        }
    }

    infix fun or(anotherRule: ExactStringRule): OneOfStringRule {
        return OneOfStringRule(listOf(string, anotherRule.string))
    }

    infix fun or(string: String): OneOfStringRule {
        return OneOfStringRule(listOf(this.string, string))
    }

    override fun clone(): ExactStringRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): ExactStringRule {
        return DebugExactStringRule(name ?: "str($string)", string)
    }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun ignoreCallbacks(): ExactStringRule {
        return this
    }
}

private class DebugExactStringRule(override val name: String, string: String) :
    ExactStringRule(string),
    DebugRule
{
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}