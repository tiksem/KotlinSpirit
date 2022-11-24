package com.kotlinspirit.str

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.str.oneof.OneOfStringRule

open class ExactStringRule(
    internal val string: String,
    name: String? = null
) : RuleWithDefaultRepeat<CharSequence>(name) {
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
        val selfLength = self.length
        if (seek + selfLength > string.length) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
            )
            return
        }

        if (string.regionMatches(
                thisOffset = seek,
                other = self,
                otherOffset = 0,
                length = selfLength
            )) {
            result.parseResult = createComplete(seek + selfLength)
            result.data = this.string
        } else {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.STRING_DOES_NOT_MATCH
            )
            result.data = null
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return string.regionMatches(
            thisOffset = seek,
            other = this.string,
            otherOffset = 0,
            length = this.string.length
        )
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

    override fun name(name: String): ExactStringRule {
        return ExactStringRule(string, name)
    }

    override val defaultDebugName: String
        get() = "str:$string"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun ignoreCallbacks(): ExactStringRule {
        return this
    }
}

class EmptyStringRule(name: String? = null): ExactStringRule("", name) {
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

    override val defaultDebugName: String
        get() = "emptyString"
}