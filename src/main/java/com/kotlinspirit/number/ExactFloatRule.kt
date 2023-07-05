package com.kotlinspirit.number

import com.kotlinspirit.str.ExactStringRepresentationRule

class ExactFloatRule(
    value: Float,
    name: String? = null
) : ExactStringRepresentationRule<Float>(value, name) {
    override fun clone(): ExactFloatRule {
        return this
    }

    override fun name(name: String): ExactFloatRule {
        return ExactFloatRule(obj, name)
    }

    override val defaultDebugName: String
        get() = "float($obj)"
}