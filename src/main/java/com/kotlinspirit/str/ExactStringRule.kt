package com.kotlinspirit.str

import com.kotlinspirit.core.*
import com.kotlinspirit.expressive.StringOrRule

internal fun exactStringParse(
    seek: Int,
    string: CharSequence,
    token: CharSequence,
    ignoreCase: Boolean
): ParseSeekResult {
    val tokenLength = token.length
    if (seek + tokenLength > string.length) {
        return ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
        )
    }

    return if (string.regionMatches(
            thisOffset = seek,
            other = token,
            otherOffset = 0,
            ignoreCase = ignoreCase,
            length = tokenLength
        )) {
        ParseSeekResult(seek + tokenLength)
    } else {
        ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.STRING_DOES_NOT_MATCH
        )
    }
}

internal fun exactStringParseWithResult(
    seek: Int,
    string: CharSequence,
    result: ParseResult<CharSequence>,
    token: CharSequence,
    ignoreCase: Boolean
) {
    val tokenLength = token.length
    if (seek + tokenLength > string.length) {
        result.parseResult = ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
        )
        return
    }

    if (string.regionMatches(
            thisOffset = seek,
            other = token,
            otherOffset = 0,
            length = tokenLength,
            ignoreCase = ignoreCase
        )) {
        result.parseResult = ParseSeekResult(seek + tokenLength)
        result.data = if (ignoreCase) {
            string.subSequence(seek, seek + token.length)
        } else {
            token
        }
    } else {
        result.parseResult = ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.STRING_DOES_NOT_MATCH
        )
        result.data = null
    }
}

internal fun exactStringReverseParse(
    seek: Int,
    string: CharSequence,
    token: CharSequence,
    ignoreCase: Boolean
): ParseSeekResult {
    val tokenLength = token.length
    if (seek + 1 < tokenLength) {
        return ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
        )
    }

    return if (string.regionMatches(
            thisOffset = seek - tokenLength + 1,
            other = token,
            otherOffset = 0,
            ignoreCase = ignoreCase,
            length = tokenLength
        )) {
        ParseSeekResult(seek - tokenLength)
    } else {
        ParseSeekResult(
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
        result.parseResult = ParseSeekResult(
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
        result.parseResult = ParseSeekResult(seek - tokenLength)
        result.data = token
    } else {
        result.parseResult = ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.STRING_DOES_NOT_MATCH
        )
        result.data = null
    }
}

open class ExactStringRule(
    private val ignoreCase: Boolean = false,
    string: CharSequence,
    name: String? = null
) : BaseExactStringRule<CharSequence>(string, name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return exactStringParse(seek, string, this.string, ignoreCase)
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<CharSequence>
    ) {
        exactStringParseWithResult(
            seek = seek,
            string = string,
            token = this.string,
            result = result,
            ignoreCase = ignoreCase
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        exactStringReverseParseWithResult(
            seek = seek,
            string = string,
            token = this.string,
            result = result
        )
    }

    override infix fun or(string: String): StringOrRule {
        return StringOrRule(this, ExactStringRule(ignoreCase, string))
    }

    override fun clone(): ExactStringRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): ExactStringRule {
        return ExactStringRule(ignoreCase, string, name)
    }

    override val defaultDebugName: String
        get() = "${if (ignoreCase) "istr" else "str"}:$string"
}

class EmptyStringRule(name: String? = null): ExactStringRule(ignoreCase = false,"", name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return ParseSeekResult(seek)
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<CharSequence>
    ) {
        result.parseResult = ParseSeekResult(seek)
        result.data = ""
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
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