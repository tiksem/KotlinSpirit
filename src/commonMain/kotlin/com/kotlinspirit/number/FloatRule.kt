package com.kotlinspirit.number

import com.kotlinspirit.core.*

class FloatRule(name: String? = null) : BaseFloatRule<Float>(
    name = name,
    invalidFloatErrorCode = ParseCode.INVALID_FLOAT
) {
    override fun String.getValue(): Float {
        // TODO: Optimize
        return when {
            isNan() -> Float.NaN
            isNegativeInfinity() -> Float.NEGATIVE_INFINITY
            isPositiveInfinity() -> Float.POSITIVE_INFINITY
            else -> toFloat()
        }
    }

    override fun clone(): FloatRule {
        return this
    }

    override fun name(name: String): FloatRule {
        return FloatRule(name)
    }

    override val defaultDebugName: String
        get() = "float"
}