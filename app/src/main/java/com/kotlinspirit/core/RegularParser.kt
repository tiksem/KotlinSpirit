package com.kotlinspirit.core

internal class RegularParser<T : Any>(originalRule: Rule<T>) : BaseParser<T>(originalRule) {
    override fun getRule(): Rule<T> {
        return originRule
    }
}