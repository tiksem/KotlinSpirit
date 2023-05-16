package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class ShortRule(name: String? = null) : RuleWithDefaultRepeat<Short>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        val length = string.length
        if (seek >= length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        var i = seek
        var result: Short = 0
        var sign: Short = 1
        var successFlag = false
        do {
            val char = string[i++]
            when {
                char == '-' -> {
                    if (successFlag) {
                        return createComplete(i)
                    } else if (sign == 1.toShort()) {
                        sign = -1
                    }
                }
                char == '+' -> {
                    if (successFlag) {
                        return createComplete(i)
                    }
                }
                char in '0'..'9' -> {
                    successFlag = true
                    result = (result * 10).toShort()
                    result = (result + (char - '0').toShort()).toShort()
                    // check int bounds
                    if (result < 0) {
                        return createStepResult(
                            seek = i,
                            parseCode = ParseCode.SHORT_OUT_OF_BOUNDS
                        )
                    }
                }
                successFlag -> {
                    return createComplete(i - 1)
                }
                else -> {
                    return createStepResult(
                        seek = i,
                        parseCode = ParseCode.INVALID_SHORT
                    )
                }
            }
        } while (i < length)

        return if (successFlag) {
            createComplete(i)
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.INVALID_SHORT
            )
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<Short>) {
        val length = string.length
        if (seek >= length) {
            r.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            return
        }

        var i = seek
        var sign: Short = 1
        var result: Short = 0
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
                                parseCode = ParseCode.INVALID_SHORT
                            )
                            return
                        }
                    }
                }
                char in '0'..'9' -> {
                    successFlag = true
                    result = (result * 10).toShort()
                    result = (result + (char - '0').toShort()).toShort()
                    // check int bounds
                    if (result < 0) {
                        r.parseResult = createStepResult(
                            seek = i,
                            parseCode = ParseCode.SHORT_OUT_OF_BOUNDS
                        )
                        return
                    }
                }
                successFlag -> {
                    r.data = (result * sign).toShort()
                    r.parseResult = createComplete(i - 1)
                    return
                }
                else -> {
                    r.parseResult = createStepResult(
                        seek = i,
                        parseCode = ParseCode.INVALID_SHORT
                    )
                    return
                }
            }
        } while (i < length)

        if (successFlag) {
            r.data = (result * sign).toShort()
            r.parseResult = createComplete(i)
        } else {
            r.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.INVALID_SHORT
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun clone(): ShortRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): ShortRule {
        return ShortRule(name)
    }

    override val defaultDebugName: String
        get() = "short"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun ignoreCallbacks(): ShortRule {
        return this
    }

    override fun getPrefixMaxLength(): Int {
        return 2
    }

    override fun isPrefixFixedLength(): Boolean {
        return false
    }
}