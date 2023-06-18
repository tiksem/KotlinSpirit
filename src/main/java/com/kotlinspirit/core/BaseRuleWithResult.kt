package com.kotlinspirit.core

abstract class BaseRuleWithResult<T : Any>(
    protected val rule: Rule<T>,
    protected val callback: (T) -> Unit,
    name: String?,
) : Rule<T>(name) {
    private val result = ParseResult<T>()

    private fun postParse(result: ParseResult<T>) {
        if (result.parseResult.getParseCode().isNotError()) {
            callback(result.data ?: throw IllegalStateException("result is null"))
        }
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        rule.parseWithResult(seek, string, result)
        postParse(result)
        return result.parseResult
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        postParse(result)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        rule.reverseParseWithResult(seek, string, result)
        postParse(result)
        return result.parseResult
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.reverseParseWithResult(seek, string, result)
        postParse(result)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.reverseHasMatch(seek, string)
    }

    override fun isThreadSafe(): Boolean {
        return false
    }

    override val defaultDebugName: String
        get() = "result(${rule.debugName})"
}