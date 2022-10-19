package com.kotlinspirit.core

import java.util.concurrent.ConcurrentHashMap

internal class ThreadSafeParser<T : Any>(originRule: Rule<T>): BaseParser<T>(originRule) {
    private val ruleMap = ConcurrentHashMap<Thread, Rule<T>>().also {
        it[Thread.currentThread()] = originRule
    }

    override fun getRule(): Rule<T> {
        return ruleMap.getOrPut(Thread.currentThread()) {
            originRule.clone()
        }
    }
}