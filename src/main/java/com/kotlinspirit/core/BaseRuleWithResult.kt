package com.kotlinspirit.core

abstract class BaseRuleWithResult<T : Any>(
    protected val rule: Rule<T>,
    protected val callback: (T) -> Unit
) : Rule<T>() {
    private val result = ParseResult<T>()

    override fun parse(seek: Int, string: CharSequence): Long {
        rule.parseWithResult(seek, string, result)
        if (result.parseResult.getParseCode().isNotError()) {
            callback(result.data ?: throw IllegalStateException("result is null"))
        }

        return result.parseResult
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (result.parseResult.getParseCode().isNotError()) {
            callback(result.data ?: throw IllegalStateException("result is null"))
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
    }

    override fun isThreadSafe(): Boolean {
        return false
    }
}