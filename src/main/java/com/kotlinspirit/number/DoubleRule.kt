package com.kotlinspirit.number

import com.kotlinspirit.core.*

class DoubleRule(name: String? = null) : BaseFloatRule<Double>(
    name = name,
    invalidFloatErrorCode = ParseCode.INVALID_DOUBLE
) {
    override fun String.getValue(): Double {
        // TODO: Optimize
        return when {
            isNan() -> Double.NaN
            isNegativeInfinity() -> Double.NEGATIVE_INFINITY
            isPositiveInfinity() -> Double.POSITIVE_INFINITY
            else -> toDouble()
        }
    }

    override fun clone(): DoubleRule {
        return this
    }

    override fun name(name: String): DoubleRule {
        return DoubleRule(name)
    }

    override val defaultDebugName: String
        get() = "double"
}