package com.example.kotlinspirit

class RuleParseCallbackWrapper<T>(
    private val rule: Rule<T>,
    private val callback: (T) -> Unit,
    private val errorCallback: (() -> Unit)? = null
) : Rule<T> {
    override fun parse(state: ParseState, requireResult: Boolean) {
        rule.parse(state, true)
        if (state.hasError) {
            errorCallback?.invoke()
        } else {
            callback(getResult(state))
        }
    }

    override fun getResult(array: CharArray, seekBegin: Int, seekEnd: Int): T {
        return rule.getResult(array, seekBegin, seekEnd)
    }
}