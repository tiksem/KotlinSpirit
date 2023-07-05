package com.kotlinspirit.number

import com.kotlinspirit.str.ExactStringRepresentationRule

class ExactDoubleRule(
    value: Double,
    name: String? = null
) : ExactStringRepresentationRule<Double>(value, name) {
    override fun clone(): ExactDoubleRule {
        return this
    }

    override fun name(name: String): ExactDoubleRule {
        return ExactDoubleRule(obj, name)
    }

    override val defaultDebugName: String
        get() = "double($obj)"
}