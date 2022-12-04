package com.kotlinspirit.grammar

import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine

abstract class Grammar<T : Any> {
    private var r: Rule<*>? = null
    abstract val result: T
    abstract fun defineRule(): Rule<*>
    open fun resetResult() {}

    internal open fun initRule(): Rule<*> {
        var rule = this.r
        if (rule == null) {
            rule = defineRule()
            this.r = rule
        }

        return rule
    }

    open fun clone(): Grammar<T> {
        val constructor = javaClass.declaredConstructors[0]
        constructor.isAccessible = true
        return constructor.newInstance() as Grammar<T>
    }

    fun toRule(): GrammarRule<T> {
        return GrammarRule(this, null)
    }
}

internal class DebugGrammar<T : Any>(
    private val grammar: Grammar<T>,
    private val engine: DebugEngine
) : Grammar<T>() {
    override val result: T
        get() = grammar.result

    override fun defineRule(): Rule<*> {
        return grammar.defineRule().debug(engine)
    }

    override fun resetResult() {
        grammar.resetResult()
    }

    override fun clone(): DebugGrammar<T> {
        return DebugGrammar(grammar.clone(), engine)
    }
}

