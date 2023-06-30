package com.kotlinspirit.bool

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

private const val TRUE_LENGTH = "true".length
private const val FALSE_LENGTH = "false".length

class BooleanRule(name: String? = null) : RuleWithDefaultRepeat<Boolean>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        if (seek >= string.length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        if (string.startsWith("true", startIndex = seek)) {
            return createComplete(seek + TRUE_LENGTH)
        }

        if (string.startsWith("false", startIndex = seek)) {
            return createComplete(seek + FALSE_LENGTH)
        }

        return createStepResult(
            seek = seek,
            parseCode = ParseCode.BOOLEAN_NO_MATCH
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Boolean>) {
        if (seek >= string.length) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            result.data = null
            return
        }

        if (string.startsWith("true", startIndex = seek)) {
            result.parseResult = createComplete(seek + TRUE_LENGTH)
            result.data = true
            return
        }

        if (string.startsWith("false", startIndex = seek)) {
            result.parseResult = createComplete(seek + FALSE_LENGTH)
            result.data = false
            return
        }

        result.parseResult = createStepResult(
            seek = seek,
            parseCode = ParseCode.BOOLEAN_NO_MATCH
        )
        result.data = null
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return string.startsWith("true") || string.startsWith("false")
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        when {
            seek < 0 -> {
                return createStepResult(
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
                    return createComplete(-1)
                }
            }
            seek < TRUE_LENGTH - 1 -> {
                return createStepResult(
                    seek = seek,
                    parseCode = ParseCode.BOOLEAN_NO_MATCH
                )
            }
            else -> {
                if (string[seek] != 'e') {
                    return createStepResult(
                        seek = seek,
                        parseCode = ParseCode.BOOLEAN_NO_MATCH
                    )
                }

                if (
                    string[seek - 1] == 'u' &&
                    string[seek - 2] == 'r' &&
                    string[seek - 3] == 't'
                ) {
                    return createComplete(seek - TRUE_LENGTH)
                } else if (
                    string[seek - 1] == 's' &&
                    string[seek - 2] == 'l' &&
                    string[seek - 3] == 'a' &&
                    string[seek - 4] == 'f'
                ) {
                    return createComplete(seek - FALSE_LENGTH)
                }
            }
        }

        return createStepResult(
            seek = seek,
            parseCode = ParseCode.BOOLEAN_NO_MATCH
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<Boolean>) {
        when {
            seek < 0 -> {
                result.parseResult = createStepResult(
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
                    result.parseResult = createComplete(-1)
                    result.data = true
                    return
                }
            }
            seek < TRUE_LENGTH - 1 -> {
                result.parseResult = createStepResult(
                    seek = seek,
                    parseCode = ParseCode.BOOLEAN_NO_MATCH
                )
                result.data = null
                return
            }
            else -> {
                if (string[seek] != 'e') {
                    result.parseResult = createStepResult(
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
                    result.parseResult = createComplete(seek - TRUE_LENGTH)
                    result.data = true
                } else if (
                    string[seek - 1] == 's' &&
                    string[seek - 2] == 'l' &&
                    string[seek - 3] == 'a' &&
                    string[seek - 4] == 'f'
                ) {
                    result.parseResult = createComplete(seek - FALSE_LENGTH)
                    result.data = false
                    return
                }
            }
        }

        result.parseResult = createStepResult(
            seek = seek,
            parseCode = ParseCode.BOOLEAN_NO_MATCH
        )
        result.data = null
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return reverseParse(seek, string).getParseCode().isNotError()
    }

    override fun ignoreCallbacks(): BooleanRule {
        return this
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