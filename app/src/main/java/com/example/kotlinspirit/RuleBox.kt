package com.example.kotlinspirit

import java.lang.IllegalStateException

private object RuleBoxDefaultIterator : BaseParseIterator<Any>() {
    override fun getResult(context: ParseContext): Any {
        throw IllegalStateException("RuleBox should not be empty")
    }

    override fun next(context: ParseContext): Int {
        throw IllegalStateException("RuleBox should not be empty")
    }
}

class RuleBox<T>: Rule<T> {
    var rule: Rule<T>? = null

    override val iterator: ParseIterator<T>
        get() = rule?.iterator ?: RuleBoxDefaultIterator as ParseIterator<T>

    override fun parse(context: ParseContext): ParseResult<T> {
        return rule?.parse(context)
            ?: throw IllegalStateException("RuleBox should not be empty")
    }
}