package com.kotlinspirit.core

internal class RegularParser<T : Any>(private val originalRule: Rule<T>) : BaseParser<T>() {
    override fun getRule(): Rule<T> {
        return originalRule
    }
}