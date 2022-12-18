package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class ULongRule(name: String? = null) : RuleWithDefaultRepeat<ULong>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        val length = string.length
        if (seek >= length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        var i = seek
        var result = 0UL
        var successFlag = false
        do {
            val char = string[i++]
            when {
                char in '0'..'9' -> {
                    val resultBefore = result
                    successFlag = true
                    result *= 10UL
                    result += (char - '0').toULong()
                    // check int bounds
                    if (result < resultBefore) {
                        return createStepResult(
                            seek = i,
                            parseCode = ParseCode.ULONG_OUT_OF_BOUNDS
                        )
                    }
                }
                successFlag -> {
                    return createComplete(i - 1)
                }
                else -> {
                    return createStepResult(
                        seek = i,
                        parseCode = ParseCode.INVALID_ULONG
                    )
                }
            }
        } while (i < length)

        return createComplete(i)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<ULong>) {
        val length = string.length
        if (seek >= length) {
            r.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            return
        }

        var i = seek
        var result = 0UL
        var successFlag = false
        do {
            val char = string[i++]
            when {
                char in '0'..'9' -> {
                    successFlag = true
                    val resultBefore = result
                    result *= 10UL
                    result += (char - '0').toULong()
                    // check int bounds
                    if (result < resultBefore) {
                        r.parseResult = createStepResult(
                            seek = i,
                            parseCode = ParseCode.ULONG_OUT_OF_BOUNDS
                        )
                        return
                    }
                }
                successFlag -> {
                    r.data = result.toULong()
                    r.parseResult = createComplete(i - 1)
                    return
                }
                else -> {
                    r.parseResult = createStepResult(
                        seek = i,
                        parseCode = ParseCode.INVALID_ULONG
                    )
                    return
                }
            }
        } while (i < length)

        r.parseResult = createComplete(i)
        r.data = result.toULong()
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun clone(): ULongRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): ULongRule {
        return ULongRule(name)
    }

    override val defaultDebugName: String
        get() = "ulong"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun ignoreCallbacks(): ULongRule {
        return this
    }
}