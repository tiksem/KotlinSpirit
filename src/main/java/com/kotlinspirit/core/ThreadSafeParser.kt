package com.kotlinspirit.core

internal open class ThreadSafeParser<T : Any>(private val originRule: Rule<T>): BaseParser<T>() {
    private val originalRuleThreadId = Thread.currentThread().id
    private val rule = ThreadLocal.withInitial { originRule.clone() }

    override fun getRule(string: CharSequence): Rule<T> {
        if (originalRuleThreadId == Thread.currentThread().id) {
            return originRule
        }

        return rule.get()
    }
}