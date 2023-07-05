package com.kotlinspirit.number

import com.kotlinspirit.str.ExactStringRepresentationRule

class ExactLongRule(
    value: Long,
    name: String? = null
) : ExactStringRepresentationRule<Long>(value, name) {
    override fun clone(): ExactLongRule {
        return this
    }

    override fun name(name: String): ExactLongRule {
        return ExactLongRule(obj, name)
    }

    override val defaultDebugName: String
        get() = "long($obj)"
}