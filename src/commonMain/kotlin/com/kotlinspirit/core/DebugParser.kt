package com.kotlinspirit.core

import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.RuleDebugTreeNode
import java.util.concurrent.ConcurrentHashMap

internal class DebugParser<T : Any>(originalRule: Rule<T>) : ThreadSafeParser<T>(originalRule) {
    private val engines = ConcurrentHashMap<Long, DebugEngine>()

    override fun getRule(string: CharSequence): Rule<T> {
        val rule = super.getRule(string)
        val engine = engines.getOrPut(Thread.currentThread().id) {
            DebugEngine()
        }
        engine.startDebugSession(string)
        return rule.debug(engine)
    }

    override fun getDebugTree(): RuleDebugTreeNode? {
        return engines[Thread.currentThread().id]?.root
    }

    override fun getDebugHistory(): List<RuleDebugTreeNode> {
        return engines[Thread.currentThread().id]?.history ?: emptyList()
    }
}