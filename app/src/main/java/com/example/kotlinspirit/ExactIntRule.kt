package com.example.kotlinspirit

class ExactIntRule(
    private val value: Int
) : BaseRule<Int>() {
    private val rule = ExactStringRule(value.toString())

    override fun createParseIterator(): ParseIterator<Int> {
        return rule.createParseIterator().transform {
            value
        }
    }
}