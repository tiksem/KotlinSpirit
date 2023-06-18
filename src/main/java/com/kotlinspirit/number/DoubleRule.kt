package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class DoubleRule(name: String? = null) : BaseFloatRule<Double>(
    name = name,
    invalidFloatErrorCode = ParseCode.INVALID_DOUBLE
) {
    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Double>) {
        FloatParsers.parseWithResult(
            seek = seek,
            string = string,
            invalidFloatErrorCode = invalidFloatErrorCode
        ) { value, parseResult ->
            result.data = value
            result.parseResult = parseResult
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<Double>) {
        FloatParsers.reverseParseWithResult(
            seek = seek,
            string = string,
            invalidFloatErrorCode = invalidFloatErrorCode
        ) { value, parseResult ->
            result.data = value
            result.parseResult = parseResult
        }
    }

    override fun clone(): DoubleRule {
        return this
    }

    override fun ignoreCallbacks(): DoubleRule {
        return this
    }

    override fun name(name: String): DoubleRule {
        return DoubleRule(name)
    }

    override val defaultDebugName: String
        get() = "double"
}