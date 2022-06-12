package com.example.kotlinspirit

class RuleWithResult<T : Any>(
    private val rule: Rule<T>,
    private val callback: (T) -> Unit
) : Rule<T> by rule {
    private val result = ParseResult<T>()

    override fun parse(seek: Int, string: CharSequence): Int {
        rule.parseWithResult(seek, string, result)
        if (result.errorCodeOrSeek >= 0) {
            callback(result.data ?: throw IllegalStateException("result is null"))
        }

        return result.errorCodeOrSeek
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (result.errorCodeOrSeek >= 0) {
            callback(result.data ?: throw IllegalStateException("result is null"))
        }
    }

    override fun notifyParseStepComplete(string: CharSequence) {
        callback(getStepParserResult(string))
    }

    override fun clone(): RuleWithResult<T> {
        return RuleWithResult(rule.clone(), callback)
    }
}