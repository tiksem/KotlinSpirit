package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

open class LongRule : RuleWithDefaultRepeat<Long>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        val length = string.length
        if (seek >= length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        var i = seek
        var result = 0L
        var sign = 1L
        var successFlag = false
        do {
            val char = string[i++]
            when {
                char == '-' -> {
                    if (successFlag) {
                        return createComplete(i)
                    } else if (sign == 1L) {
                        sign = -1L
                    }
                }
                char == '+' -> {
                    if (successFlag) {
                        return createComplete(i)
                    }
                }
                !successFlag && char == '0' -> {
                    return if (i >= length || string[i] !in '0'..'9') {
                        createComplete(i)
                    } else {
                        createStepResult(
                            seek = i,
                            parseCode = ParseCode.INT_STARTED_FROM_ZERO
                        )
                    }
                }
                char in '0'..'9' -> {
                    successFlag = true
                    result *= 10
                    result += char - '0'
                    // check int bounds
                    if (result < 0) {
                        return createStepResult(
                            seek = i,
                            parseCode = ParseCode.LONG_OUT_OF_BOUNDS
                        )
                    }
                }
                successFlag -> {
                    return createComplete(i - 1)
                }
                else -> {
                    return createStepResult(
                        seek = i,
                        parseCode = ParseCode.INVALID_INT
                    )
                }
            }
        } while (i < length)

        return if (successFlag) {
            createComplete(i)
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.INVALID_LONG
            )
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<Long>) {
        val length = string.length
        if (seek >= length) {
            r.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            return
        }

        var i = seek
        var sign = 1L
        var result = 0L
        var successFlag = false
        do {
            val char = string[i++]
            when {
                (char == '-' || char == '+') && !successFlag -> {
                    when {
                        i == seek + 1 -> {
                            if (char == '-') {
                                sign = -1
                            }
                        }
                        else -> {
                            r.parseResult = createStepResult(
                                seek = i,
                                parseCode = ParseCode.INVALID_INT
                            )
                            return
                        }
                    }
                }
                !successFlag && char == '0' -> {
                    if (i >= length || string[i] !in '0'..'9') {
                        r.data = result * sign
                        r.parseResult = createComplete(i)
                    } else {
                        r.parseResult = createStepResult(
                            seek = i,
                            parseCode = ParseCode.INT_STARTED_FROM_ZERO
                        )
                    }
                    return
                }
                char in '0'..'9' -> {
                    successFlag = true
                    result *= 10
                    result += char - '0'
                    // check int bounds
                    if (result < 0) {
                        r.parseResult = createStepResult(
                            seek = i,
                            parseCode = ParseCode.INT_OUT_OF_BOUNDS
                        )
                        return
                    }
                }
                successFlag -> {
                    r.data = result * sign
                    r.parseResult = createComplete(i - 1)
                    return
                }
                else -> {
                    r.parseResult = createStepResult(
                        seek = i,
                        parseCode = ParseCode.INVALID_INT
                    )
                    return
                }
            }
        } while (i < length)

        if (successFlag) {
            r.data = result * sign
            r.parseResult = createComplete(i)
        } else {
            r.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.INVALID_INT
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val length = string.length
        if (seek >= length) {
            return false
        }

        val char = string[seek]
        return if (char == '-') {
            seek < length - 1 && string[seek + 1] in '0'..'9'
        } else {
            char in '0'..'9'
        }
    }

    override fun clone(): LongRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): LongRule {
        return DebugLongRule(name ?: "long")
    }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun ignoreCallbacks(): LongRule {
        return this
    }
}

private class DebugLongRule(override val name: String): LongRule(), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<Long>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, r)
        DebugEngine.ruleParseEnded(this, r.parseResult)
    }
}