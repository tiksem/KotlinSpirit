package com.kotlinspirit.grammar

import com.kotlinspirit.core.NullBox
import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules.grammar

inline fun <T : Any> nestedResult(
    crossinline nested: () -> Rule<T>,
    crossinline entire: (Rule<T>) -> Rule<*>
): GrammarRule<T, NullBox<T>> {
    return grammar(
        defineRule = { data -> entire(nested().invoke { data.value = it }) },
        getResult = { it.value!! },
        dataFactory = { NullBox(null) }
    )
}

inline fun <T : Any> nestedResult(
    nested: Rule<T>,
    crossinline entire: (Rule<T>) -> Rule<*>
): GrammarRule<T, NullBox<T>> {
    return nestedResult(
        nested = {
            nested.clone()
        },
        entire = entire
    )
}