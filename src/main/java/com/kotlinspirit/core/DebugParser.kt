package com.kotlinspirit.core

import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.RuleDebugTreeNode
import com.kotlinspirit.ext.ConcurrentReferenceHashMap

internal class DebugParser<T : Any>(originalRule: Rule<T>) : ThreadSafeParser<T>(originalRule) {
    private val engines = ConcurrentReferenceHashMap<Thread, DebugEngine>(
        16, ConcurrentReferenceHashMap.ReferenceType.WEAK
    )

    override fun getRule(string: CharSequence): Rule<T> {
        val rule = super.getRule(string)
        val engine = engines.getOrPut(Thread.currentThread()) {
            DebugEngine()
        }
        engine.startDebugSession(string)
        return rule.debug(engine)
    }

    override fun getDebugTree(): RuleDebugTreeNode? {
        return engines[Thread.currentThread()]?.root
    }

    override fun getDebugHistory(): List<RuleDebugTreeNode> {
        return engines[Thread.currentThread()]?.history ?: emptyList()
    }
}