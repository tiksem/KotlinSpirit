package com.kotlinspirit.grammar

import com.kotlinspirit.core.Rule

abstract class Grammar<T : Any> {
    private var r: Rule<*>? = null
    abstract val result: T
    abstract fun defineRule(): Rule<*>
    open fun resetResult() {}

    internal fun initRule(): Rule<*> {
        var rule = this.r
        if (rule == null) {
            rule = defineRule()
            this.r = rule
        }

        return rule
    }

    fun clone(): Grammar<T> {
        val constructor = javaClass.declaredConstructors[0]
        constructor.isAccessible = true
        return constructor.newInstance() as Grammar<T>
    }

    fun toRule(): GrammarRule<T> {
        return GrammarRule(this, null)
    }
}

