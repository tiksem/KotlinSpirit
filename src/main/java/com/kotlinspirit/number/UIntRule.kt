package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

open class UIntRule : RuleWithDefaultRepeat<UInt>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        val length = string.length
        if (seek >= length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        var i = seek
        var result = 0
        var successFlag = false
        do {
            val char = string[i++]
            when {
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
                            parseCode = ParseCode.INT_OUT_OF_BOUNDS
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
        var result = 0
        var successFlag = false
        do {
            val char = string[i++]
            when {
                !successFlag && char == '0' -> {
                    if (i >= length || string[i] !in '0'..'9') {
                        r.data = 0u
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
                    r.data = result.toUInt()
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

        r.parseResult = createComplete(i)
        r.data = result.toUInt()
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

    override fun clone(): UIntRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): UIntRule {
        return DebugUIntRule(name ?: "uint")
    }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun ignoreCallbacks(): UIntRule {
        return this
    }
}

private class DebugUIntRule(override val name: String): UIntRule(), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<UInt>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, r)
        DebugEngine.ruleParseEnded(this, r.parseResult)
    }
}