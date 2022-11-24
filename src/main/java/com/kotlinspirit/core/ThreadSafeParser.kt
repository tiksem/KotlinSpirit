package com.kotlinspirit.core

import java.util.concurrent.ConcurrentHashMap

internal open class ThreadSafeParser<T : Any>(private val originRule: Rule<T>): BaseParser<T>() {
    private val ruleMap = ConcurrentHashMap<Long, Rule<T>>().also {
        it[Thread.currentThread().id] = originRule
    }

    override fun getRule(string: CharSequence): Rule<T> {
        return ruleMap.getOrPut(Thread.currentThread().id) {
            originRule.clone()
        }
    }
}