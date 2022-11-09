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
        if (seek >= string.length) {
            return createStepResult(
                seek = seek,
                ParseCode.EOF
            )
        }

        val self = this.string
        val selfLength = self.length
        if (seek + selfLength > string.length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
            )
        }

        return if (string.regionMatches(
                thisOffset = seek,
                other = self,
                otherOffset = 0,
                length = selfLength
            )) {
            createComplete(seek + selfLength)
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
        if (seek >= string.length) {
            result.parseResult = createStepResult(
                seek = seek,
                ParseCode.EOF
            )
            return
        }

        val self = this.string
        if (seek + self.length > string.length) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
            )
            return
        }

        var i = seek
        val end = self.length + seek
        do {
            if (self[i - seek] != string[i]) {
                result.parseResult = createStepResult(
                    seek = seek,
                    parseCode = ParseCode.STRING_DOES_NOT_MATCH
                )
                return
            }
            i++
        } while (i < end)

        result.parseResult = createComplete(end)
        result.data = this.string
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val self = this.string
        if (seek + self.length > string.length) {
            return false
        }

        var i = seek
        val end = string.length + seek
        do {
            if (self[i] != string[i]) {
                return false
            }
            i++
        } while (i < end)

        return true
    }

    infix fun or(anotherRule: ExactStringRule): OneOfStringRule {
        return OneOfStringRule(listOf(string, anotherRule.string))
    }

    override infix fun or(string: String): OneOfStringRule {
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

open class EmptyStringRule: ExactStringRule("") {
    override fun parse(seek: Int, string: CharSequence): Long {
        return createComplete(seek)
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<CharSequence>
    ) {
        result.parseResult = createComplete(seek)
        result.data = ""
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun debug(name: String?): ExactStringRule {
        return DebugEmptyStringRule(name ?: "str()")
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

private class DebugEmptyStringRule(override val name: String) : EmptyStringRule(), DebugRule {
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