package com.kotlinspirit.core

import com.kotlinspirit.platform.createThreadLocal

internal open class ThreadSafeParser<T : Any>(private val originRule: Rule<T>): BaseParser<T>() {
    private val rule = createThreadLocal { originRule.clone() }

    override fun getRule(string: CharSequence): Rule<T> {
        return rule.get()
    }
}