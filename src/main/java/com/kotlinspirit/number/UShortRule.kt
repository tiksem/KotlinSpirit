package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class UShortRule(name: String? = null) : RuleWithDefaultRepeat<UShort>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        val length = string.length
        if (seek >= length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        var i = seek
        var result = 0.toUShort()
        var successFlag = false
        do {
            val char = string[i++]
            when {
                char in '0'..'9' -> {
                    val resultBefore = result
                    successFlag = true
                    result = (result * 10u).toUShort()
                    result = (result + (char - '0').toUShort()).toUShort()
                    // check int bounds
                    if (result < resultBefore) {
                        return createStepResult(
                            seek = i,
                            parseCode = ParseCode.USHORT_OUT_OF_BOUNDS
                        )
                    }
                }
                successFlag -> {
                    return createComplete(i - 1)
                }
                else -> {
                    return createStepResult(
                        seek = i,
                        parseCode = ParseCode.INVALID_USHORT
                    )
                }
            }
        } while (i < length)

        return createComplete(i)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<UShort>) {
        val length = string.length
        if (seek >= length) {
            r.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            return
        }

        var i = seek
        var result = 0.toUShort()
        var successFlag = false
        do {
            val char = string[i++]
            when {
                char in '0'..'9' -> {
                    val resultBefore = result
                    successFlag = true
                    result = (result * 10u).toUShort()
                    result = (result + (char - '0').toUShort()).toUShort()
                    // check int bounds
                    if (result < resultBefore) {
                        r.parseResult = createStepResult(
                            seek = i,
                            parseCode = ParseCode.USHORT_OUT_OF_BOUNDS
                        )
                        return
                    }
                }
                successFlag -> {
                    r.data = result
                    r.parseResult = createComplete(i - 1)
                    return
                }
                else -> {
                    r.parseResult = createStepResult(
                        seek = i,
                        parseCode = ParseCode.INVALID_USHORT
                    )
                    return
                }
            }
        } while (i < length)

        r.parseResult = createComplete(i)
        r.data = result
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun clone(): UShortRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): UShortRule {
        return UShortRule(name)
    }

    override val defaultDebugName: String
        get() = "ushort"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun isDynamic(): Boolean {
        return false
    }

    override fun ignoreCallbacks(): UShortRule {
        return this
    }
}