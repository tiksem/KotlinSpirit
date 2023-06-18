package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import java.math.BigInteger

open class BigIntegerRule(name: String? = null) : RuleWithDefaultRepeat<BigInteger>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return IntParsers.parse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_BIG_INTEGER,
            outOfBoundsParseCode = -1, // Not used
            checkOutOfBounds = { false }
        )
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
            in '0'..'9' -> {
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

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return IntParsers.reverseParse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_BIG_INTEGER,
            outOfBoundsParseCode = -1, // Not used
            checkOutOfBounds = { false }
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<BigInteger>) {
        val res = reverseParse(seek, string)
        result.parseResult = res
        if (res.getParseCode().isError()) {
            result.data = null
        } else {
            result.data = BigInteger(string.substring(res.getSeek() + 1, seek + 1))
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return seek >= 0 && string[seek].isDigit()
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

    override fun clone(): BigIntegerRule {
        return this
    }
}