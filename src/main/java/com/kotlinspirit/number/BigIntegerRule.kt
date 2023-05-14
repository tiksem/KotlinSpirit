package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import java.math.BigInteger

open class BigIntegerRule(name: String? = null) : RuleWithDefaultRepeat<BigInteger>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        val length = string.length
        if (seek >= length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        var i = 0
        when (string[i]) {
            in '0'..'9' -> {
                i++
            }
            '+', '-' -> {
                i++
                if (i >= length) {
                    return createStepResult(
                        seek = seek,
                        parseCode = ParseCode.INVALID_BIG_INTEGER
                    )
                }

                when (string[i]) {
                    in '0'..'9' -> {
                        i++
                    }
                    else -> {
                        return createStepResult(
                            seek = seek,
                            parseCode = ParseCode.INVALID_BIG_INTEGER
                        )
                    }
                }
            }
            else -> {
                return createStepResult(
                    seek = seek,
                    parseCode = ParseCode.INVALID_BIG_INTEGER
                )
            }
        }

        while (i < length && string[i] in '0'..'9' ) {
            i++
        }

        return createComplete(i)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<BigInteger>) {
        val res = parse(seek, string)
        result.parseResult = res
        if (res.getParseCode().isError()) {
            result.data = null
        } else {
            result.data = BigInteger(string.substring(seek, res.getSeek()))
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val length = string.length
        if (seek >= length) {
            return false
        }

        return when (string[seek]) {
            '0' -> {
                seek == length - 1 || string[seek + 1] !in '0'..'9'
            }
            in '1'..'9' -> {
               true
            }
            '+', '-' -> {
                if (seek == length - 1) {
                    return false
                }

                when (string[seek + 1]) {
                    in '0'..'9' -> {
                        true
                    }
                    else -> false
                }
            }
            else -> false
        }
    }

    override fun ignoreCallbacks(): BigIntegerRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): BigIntegerRule {
        return BigIntegerRule(name)
    }

    override val defaultDebugName: String
        get() = "bigint"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun isDynamic(): Boolean {
        return false
    }

    override fun clone(): BigIntegerRule {
        return this
    }
}