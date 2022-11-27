package com.kotlinspirit.core

import org.springframework.util.ConcurrentReferenceHashMap
import org.springframework.util.ConcurrentReferenceHashMap.ReferenceType

internal open class ThreadSafeParser<T : Any>(private val originRule: Rule<T>): BaseParser<T>() {
    private val ruleMap = ConcurrentReferenceHashMap<Thread, Rule<T>>(
        16, ReferenceType.WEAK
    ).also {
        it[Thread.currentThread()] = originRule
    }

    override fun getRule(string: CharSequence): Rule<T> {
        return ruleMap.getOrPut(Thread.currentThread()) {
            originRule.clone()
        }
    }
}