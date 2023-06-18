package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class FloatRule(name: String? = null) : BaseFloatRule<Float>(
    name = name,
    invalidFloatErrorCode = ParseCode.INVALID_FLOAT
) {
    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Float>) {
        FloatParsers.parseWithResult(
            seek = seek,
            string = string,
            invalidFloatErrorCode = invalidFloatErrorCode
        ) { value, parseResult ->
            result.data = value?.toFloat()
            result.parseResult = parseResult
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<Float>) {
        FloatParsers.reverseParseWithResult(
            seek = seek,
            string = string,
            invalidFloatErrorCode = invalidFloatErrorCode,
            result = { value, parseResult ->
                result.data = value?.toFloat()
                result.parseResult = parseResult
            }
        )
    }

    override fun clone(): FloatRule {
        return this
    }

    override fun ignoreCallbacks(): FloatRule {
        return this
    }

    override fun name(name: String): FloatRule {
        return FloatRule(name)
    }

    override val defaultDebugName: String
        get() = "float"

    companion object {
        internal const val MAX_FLOAT_PREFIX_LENGTH = "-infinity".length
    }
}