package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class UIntRule(name: String? = null) : RuleWithDefaultRepeat<UInt>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        val length = string.length
        if (seek >= length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        var i = seek
        var result = 0u
        var successFlag = false
        do {
            val char = string[i++]
            when {
                char in '0'..'9' -> {
                    val resultBefore = result
                    successFlag = true
                    result *= 10u
                    result += (char - '0').toUInt()
                    // check int bounds
                    if (result < resultBefore) {
                        return createStepResult(
                            seek = i,
                            parseCode = ParseCode.UINT_OUT_OF_BOUNDS
                        )
                    }
                }
                successFlag -> {
                    return createComplete(i - 1)
                }
                else -> {
                    return createStepResult(
                        seek = i,
                        parseCode = ParseCode.INVALID_UINT
                    )
                }
            }
        } while (i < length)

        return createComplete(i)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<UInt>) {
        val length = string.length
        if (seek >= length) {
            r.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            return
        }

        var i = seek
        var result = 0u
        var successFlag = false
        do {
            val char = string[i++]
            when {
                char in '0'..'9' -> {
                    val resultBefore = result
                    successFlag = true
                    result *= 10u
                    result += (char - '0').toUInt()
                    // check int bounds
                    if (result < resultBefore) {
                        r.parseResult = createStepResult(
                            seek = i,
                            parseCode = ParseCode.UINT_OUT_OF_BOUNDS
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
                        parseCode = ParseCode.INVALID_UINT
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

    override fun clone(): UIntRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): UIntRule {
        return UIntRule(name)
    }

    override val defaultDebugName: String
        get() = "uint"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun isDynamic(): Boolean {
        return false
    }

    override fun ignoreCallbacks(): UIntRule {
        return this
    }
}