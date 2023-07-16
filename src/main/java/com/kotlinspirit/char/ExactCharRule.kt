package com.kotlinspirit.char

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.ParseSeekResult

open class ExactCharRule(
    private val char: Char
) : CharPredicateRule(data = CharPredicateData(chars = charArrayOf(char))) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        if (seek >= string.length) {
            return ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        return if (string[seek] == char) {
            ParseSeekResult(
                seek = seek + 1,
                parseCode = ParseCode.COMPLETE
            )
        } else {
            ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        if (seek >= string.length) {
            result.parseResult = ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            return
        }

        if (string[seek] == char) {
            result.parseResult = ParseSeekResult(
                seek = seek + 1,
                parseCode = ParseCode.COMPLETE
            )
            result.data = char
        } else {
            result.parseResult = ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
            result.data = null
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < string.length && string[seek] == char
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        if (seek < 0) {
            return ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        return if (string[seek] == char) {
            ParseSeekResult(
                seek = seek - 1,
                parseCode = ParseCode.COMPLETE
            )
        } else {
            ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        if (seek < 0) {
            result.parseResult = ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            return
        }

        if (string[seek] == char) {
            result.parseResult = ParseSeekResult(
                seek = seek - 1,
                parseCode = ParseCode.COMPLETE
            )
            result.data = char
        } else {
            result.parseResult = ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
            result.data = null
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return seek >= 0 && string[seek] == char
    }

    override fun clone(): ExactCharRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "char('$char')"
}