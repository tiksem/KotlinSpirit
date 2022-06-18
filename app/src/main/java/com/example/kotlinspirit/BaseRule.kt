package com.example.kotlinspirit

abstract class BaseRule<T : Any> : Rule<T> {
    override fun repeat(): Rule<List<T>> {
        return ZeroOrMoreRule(this)
    }
}