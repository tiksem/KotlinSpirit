package com.kotlinspirit.str

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.str.oneof.OneOfStringRule

internal fun exactStringParse(seek: Int, string: CharSequence, token: CharSequence): Long {
    val tokenLength = token.length
    if (seek + tokenLength > string.length) {
        return createStepResult(
            seek = seek,
            parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
        )
    }

    return if (string.regionMatches(
            thisOffset = seek,
            other = token,
            otherOffset = 0,
            length = tokenLength
        )) {
        createComplete(seek + tokenLength)
    } else {
        createStepResult(
            seek = seek,
            parseCode = ParseCode.STRING_DOES_NOT_MATCH
        )
    }
}

internal fun exactStringParseWithResult(
    seek: Int,
    string: CharSequence,
    result: ParseResult<CharSequence>,
    token: CharSequence
) {
    val tokenLength = token.length
    if (seek + tokenLength > string.length) {
        result.parseResult = createStepResult(
            seek = seek,
            parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
        )
        return
    }

    if (string.regionMatches(
            thisOffset = seek,
            other = token,
            otherOffset = 0,
            length = tokenLength
        )) {
        result.parseResult = createComplete(seek + tokenLength)
        result.data = token
    } else {
        result.parseResult = createStepResult(
            seek = seek,
            parseCode = ParseCode.STRING_DOES_NOT_MATCH
        )
        result.data = null
    }
}

internal fun exactStringReverseParse(seek: Int, string: CharSequence, token: CharSequence): Long {
    val tokenLength = token.length
    if (seek + 1 < tokenLength) {
        return createStepResult(
            seek = seek,
            parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
        )
    }

    return if (string.regionMatches(
            thisOffset = seek - tokenLength + 1,
            other = token,
            otherOffset = 0,
            length = tokenLength
        )) {
        createComplete(seek - tokenLength)
    } else {
        createStepResult(
            seek = seek,
            parseCode = ParseCode.STRING_DOES_NOT_MATCH
        )
    }
}

internal fun exactStringReverseParseWithResult(
    seek: Int,
    string: CharSequence,
    result: ParseResult<CharSequence>,
    token: CharSequence
) {
    val tokenLength = token.length
    if (seek + 1 < tokenLength) {
        result.parseResult = createStepResult(
            seek = seek,
            parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
        )
        return
    }

    if (string.regionMatches(
            thisOffset = seek - tokenLength + 1,
            other = token,
            otherOffset = 0,
            length = tokenLength
        )) {
        result.parseResult = createComplete(seek - tokenLength)
        result.data = token
    } else {
        result.parseResult = createStepResult(
            seek = seek,
            parseCode = ParseCode.STRING_DOES_NOT_MATCH
        )
        result.data = null
    }
}

open class ExactStringRule(
    internal val string: CharSequence,
    name: String? = null
) : RuleWithDefaultRepeat<CharSequence>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return exactStringParse(seek, string, this.string)
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<CharSequence>
    ) {
        exactStringParseWithResult(
            seek = seek,
            string = string,
            token = this.string,
            result = result
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return string.regionMatches(
            thisOffset = seek,
            other = this.string,
            otherOffset = 0,
            length = this.string.length
        )
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return exactStringReverseParse(seek, string, this.string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        exactStringReverseParseWithResult(
            seek = seek,
            string = string,
            token = this.string,
            result = result
        )
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return string.regionMatches(
            thisOffset = seek - this.string.length + 1,
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

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return parse(seek, string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        parseWithResult(seek, string, result)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override val defaultDebugName: String
        get() = "emptyString"
}