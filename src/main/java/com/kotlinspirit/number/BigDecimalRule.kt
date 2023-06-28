package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import java.math.BigDecimal

class BigDecimalRule(name: String? = null) : RuleWithDefaultRepeat<BigDecimal>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return FloatParsers.parse(
            seek = seek,
            string = string,
            invalidFloatErrorCode = ParseCode.INVALID_BIG_DECIMAL
        ).let {
            if (it.getParseCode().isError()) {
                it
            } else {
                try {
                    BigDecimal(string.substring(seek, it.getSeek()))
                    it
                } catch (e: NumberFormatException) {
                    createStepResult(
                        seek = seek,
                        parseCode = ParseCode.BIG_DECIMAL_EXPONENT_OVERFLOW
                    )
                }
            }
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<BigDecimal>) {
        val r = parse(seek = seek, string = string)
        result.parseResult = r
        if (r.getParseCode().isError()) {
            result.data = null
        } else {
            result.data = BigDecimal(string.substring(seek, r.getSeek()))
        }
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return FloatParsers.reverseParse(
            seek = seek,
            string = string,
            invalidFloatErrorCode = ParseCode.INVALID_BIG_DECIMAL
        ).let {
            if (it.getParseCode().isError()) {
                it
            } else {
                try {
                    BigDecimal(string.substring(it.getSeek() + 1, seek + 1))
                    it
                } catch (e: NumberFormatException) {
                    createStepResult(
                        seek = seek,
                        parseCode = ParseCode.BIG_DECIMAL_EXPONENT_OVERFLOW
                    )
                }
            }
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<BigDecimal>) {
        val r = reverseParse(seek = seek, string = string)
        result.parseResult = r
        if (r.getParseCode().isError()) {
            result.data = null
        } else {
            result.data = BigDecimal(string.substring(r.getSeek() + 1, seek + 1))
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return reverseParse(seek, string).getParseCode().isNotError()
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode().isNotError()
    }

    override fun ignoreCallbacks(): BigDecimalRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun clone(): BigDecimalRule {
        return this
    }

    override fun name(name: String): BigDecimalRule {
        return BigDecimalRule(name)
    }

    override val defaultDebugName: String
        get() = "bigDecimal"
}