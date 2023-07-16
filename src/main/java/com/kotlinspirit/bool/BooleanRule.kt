package com.kotlinspirit.bool

import com.kotlinspirit.core.*
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

private const val TRUE_LENGTH = "true".length
private const val FALSE_LENGTH = "false".length

class BooleanRule(name: String? = null) : RuleWithDefaultRepeat<Boolean>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        if (seek >= string.length) {
            return ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        if (string.startsWith("true", startIndex = seek)) {
            return ParseSeekResult(seek + TRUE_LENGTH)
        }

        if (string.startsWith("false", startIndex = seek)) {
            return ParseSeekResult(seek + FALSE_LENGTH)
        }

        return ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.BOOLEAN_NO_MATCH
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Boolean>) {
        if (seek >= string.length) {
            result.parseResult = ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            result.data = null
            return
        }

        if (string.startsWith("true", startIndex = seek)) {
            result.parseResult = ParseSeekResult(seek + TRUE_LENGTH)
            result.data = true
            return
        }

        if (string.startsWith("false", startIndex = seek)) {
            result.parseResult = ParseSeekResult(seek + FALSE_LENGTH)
            result.data = false
            return
        }

        result.parseResult = ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.BOOLEAN_NO_MATCH
        )
        result.data = null
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return string.startsWith("true") || string.startsWith("false")
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        when {
            seek < 0 -> {
                return ParseSeekResult(
                    seek = seek,
                    parseCode = ParseCode.EOF
                )
            }
            seek == TRUE_LENGTH - 1 -> {
                if (
                    string[seek] == 'e' &&
                    string[seek - 1] == 'u' &&
                    string[seek - 2] == 'r' &&
                    string[seek - 3] == 't'
                ) {
                    return ParseSeekResult(-1)
                }
            }
            seek < TRUE_LENGTH - 1 -> {
                return ParseSeekResult(
                    seek = seek,
                    parseCode = ParseCode.BOOLEAN_NO_MATCH
                )
            }
            else -> {
                if (string[seek] != 'e') {
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = ParseCode.BOOLEAN_NO_MATCH
                    )
                }

                if (
                    string[seek - 1] == 'u' &&
                    string[seek - 2] == 'r' &&
                    string[seek - 3] == 't'
                ) {
                    return ParseSeekResult(seek - TRUE_LENGTH)
                } else if (
                    string[seek - 1] == 's' &&
                    string[seek - 2] == 'l' &&
                    string[seek - 3] == 'a' &&
                    string[seek - 4] == 'f'
                ) {
                    return ParseSeekResult(seek - FALSE_LENGTH)
                }
            }
        }

        return ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.BOOLEAN_NO_MATCH
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<Boolean>) {
        when {
            seek < 0 -> {
                result.parseResult = ParseSeekResult(
                    seek = seek,
                    parseCode = ParseCode.EOF
                )
                result.data = null
                return
            }
            seek == TRUE_LENGTH - 1 -> {
                if (
                    string[seek] == 'e' &&
                    string[seek - 1] == 'u' &&
                    string[seek - 2] == 'r' &&
                    string[seek - 3] == 't'
                ) {
                    result.parseResult = ParseSeekResult(-1)
                    result.data = true
                    return
                }
            }
            seek < TRUE_LENGTH - 1 -> {
                result.parseResult = ParseSeekResult(
                    seek = seek,
                    parseCode = ParseCode.BOOLEAN_NO_MATCH
                )
                result.data = null
                return
            }
            else -> {
                if (string[seek] != 'e') {
                    result.parseResult = ParseSeekResult(
                        seek = seek,
                        parseCode = ParseCode.BOOLEAN_NO_MATCH
                    )
                    result.data = null
                    return
                }

                if (
                    string[seek - 1] == 'u' &&
                    string[seek - 2] == 'r' &&
                    string[seek - 3] == 't'
                ) {
                    result.parseResult = ParseSeekResult(seek - TRUE_LENGTH)
                    result.data = true
                    return
                } else if (
                    string[seek - 1] == 's' &&
                    string[seek - 2] == 'l' &&
                    string[seek - 3] == 'a' &&
                    string[seek - 4] == 'f'
                ) {
                    result.parseResult = ParseSeekResult(seek - FALSE_LENGTH)
                    result.data = false
                    return
                }
            }
        }

        result.parseResult = ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.BOOLEAN_NO_MATCH
        )
        result.data = null
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return !reverseParse(seek, string).isError
    }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun name(name: String): BooleanRule {
        return BooleanRule(name)
    }

    override fun clone(): BooleanRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "boolean"
}