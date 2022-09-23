package com.kotlinspirit.grammar

import com.kotlinspirit.core.Rule

abstract class NestedResultGrammar<T : Any> : Grammar<T>() {
    private var r: T? = null

    override val result: T
        get() = r ?: throw IllegalStateException("invalid nested grammar result access")

    override fun defineRule(): Rule<*> {
        return entire(nested().invoke {
            r = it
        })
    }

    protected abstract fun entire(nested: Rule<T>): Rule<*>
    protected abstract fun nested(): Rule<T>
}

inline fun <T : Any> nestedResult(
    crossinline nested: () -> Rule<T>,
    crossinline entire: (Rule<T>) -> Rule<*>
): GrammarRule<T> {
    return object : NestedResultGrammar<T>() {
        override fun entire(nested: Rule<T>): Rule<*> {
            return entire(nested)
        }

        override fun nested(): Rule<T> {
            return nested()
        }
    }.toRule()
}

inline fun <T : Any> nestedResult(
    nested: Rule<T>,
    crossinline entire: (Rule<T>) -> Rule<*>
): GrammarRule<T> {
    return nestedResult(
        nested = {
            nested
        },
        entire = entire
    )
}