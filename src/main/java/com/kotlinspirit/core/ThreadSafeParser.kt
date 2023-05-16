package com.kotlinspirit.core

internal open class ThreadSafeParser<T : Any>(private val originRule: Rule<T>): BaseParser<T>() {
    private val rule = ThreadLocal.withInitial { originRule.clone() }

    override fun getRule(string: CharSequence): Rule<T> {
        return rule.get()
    }
}