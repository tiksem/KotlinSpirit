package com.example.kotlinspirit

class RuleParseCallbackWrapper<T>(
    private val rule: Rule<T>,
    private val callback: (T) -> Unit,
    private val errorCallback: (() -> Unit)? = null
) : Rule<T> {
    override fun parse(state: ParseState) {
        rule.parse(state)
        if (state.hasError) {
            errorCallback?.invoke()
        } else {
            callback(getResult(state))
        }
    }

    override fun getResult(state: ParseState): T {
        return rule.getResult(state)
    }
}