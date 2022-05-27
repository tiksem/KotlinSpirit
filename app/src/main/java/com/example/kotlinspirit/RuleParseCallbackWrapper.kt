package com.example.kotlinspirit

private class RuleParseCallbackWrapperIterator<T>(
    private val iterator: ParseIterator<T>,
    private val callback: (T) -> Unit,
    private val errorCallback: (() -> Unit)? = null
) : ParseIterator<T> by iterator {
    override fun next(): Int {
        val code = iterator.next()
        if (code == StepCode.COMPLETE) {
            callback(getResult())
        } else if (code.isError()) {
            errorCallback?.invoke()
        }

        return code
    }
}

class RuleParseCallbackWrapper<T>(
    private val rule: Rule<T>,
    private val callback: (T) -> Unit,
    private val errorCallback: (() -> Unit)? = null
) : BaseRule<T>() {
    override fun createParseIterator(): ParseIterator<T> {
        return RuleParseCallbackWrapperIterator(
            iterator = rule.iterator,
            callback = callback,
            errorCallback = errorCallback
        )
    }
}