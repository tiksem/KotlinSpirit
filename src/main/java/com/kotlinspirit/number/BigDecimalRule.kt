package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import java.math.BigDecimal

open class BigDecimalRule : RuleWithDefaultRepeat<BigDecimal>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        return parseFloatingNumber(
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
                        parseCode = ParseCode.INVALID_BIG_DECIMAL
                    )
                }
            }
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<BigDecimal>) {
        val r = parseFloatingNumber(
            seek = seek,
            string = string,
            invalidFloatErrorCode = ParseCode.INVALID_BIG_DECIMAL
        )
        result.parseResult = r
        if (r.getParseCode().isError()) {
            result.data = null
        } else {
            result.data = BigDecimal(string.substring(seek, r.getSeek()))
        }
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

    override fun debug(name: String?): BigDecimalRule {
        return DebugBigDecimalRule(name ?: "bigDecimal")
    }
}

private class DebugBigDecimalRule(override val name: String): BigDecimalRule(), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<BigDecimal>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, r)
        DebugEngine.ruleParseEnded(this, r.parseResult)
    }
}