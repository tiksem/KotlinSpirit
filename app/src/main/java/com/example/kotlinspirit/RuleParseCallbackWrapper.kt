package com.example.kotlinspirit

import java.lang.IllegalStateException

class RuleParseCallbackWrapper<T>(
    private val rule: Rule<T>,
    private val callback: (T) -> Unit,
    private val errorCallback: (() -> Unit)? = null
) : Rule<T> {
    override val iterator: ParseIterator<T>
        get() = rule.iterator

    override fun parse(
        state: ParseState,
        string: CharSequence,
        requireResult: Boolean,
        maxLength: Int?
    ): T? {
        return rule.parse(state, string, true, maxLength).also {
            if (state.hasError) {
                errorCallback?.invoke()
            } else {
                callback(it ?: throw IllegalStateException("Result unavailable"))
            }
        }
    }
}