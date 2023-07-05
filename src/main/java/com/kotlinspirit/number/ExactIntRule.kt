package com.kotlinspirit.number

import com.kotlinspirit.str.ExactStringRepresentationRule

class ExactIntRule(
    value: Int,
    name: String? = null
) : ExactStringRepresentationRule<Int>(value, name) {
    override fun clone(): ExactIntRule {
        return this
    }

    override fun name(name: String): ExactIntRule {
        return ExactIntRule(obj, name)
    }

    override val defaultDebugName: String
        get() = "int($obj)"
}