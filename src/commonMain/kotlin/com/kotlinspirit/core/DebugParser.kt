package com.kotlinspirit.core

import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.RuleDebugTreeNode
import com.kotlinspirit.platform.createThreadLocal

internal class DebugParser<T : Any>(originalRule: Rule<T>) : ThreadSafeParser<T>(originalRule) {
    private val engine = createThreadLocal { DebugEngine() }

    override fun getRule(string: CharSequence): Rule<T> {
        val rule = super.getRule(string)
        val engine = engine.get()
        engine.startDebugSession(string)
        return rule.debug(engine)
    }

    override fun getDebugTree(): RuleDebugTreeNode? {
        return engine.get().root
    }

    override fun getDebugHistory(): List<RuleDebugTreeNode> {
        return engine.get().history
    }
}