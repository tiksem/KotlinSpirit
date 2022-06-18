package com.example.kotlinspirit

class RuleWithResult<T : Any>(
    private val rule: Rule<T>,
    private val callback: (T) -> Unit
) : Rule<T> {
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

    override fun resetStep() {
        rule.resetStep()
    }

    override fun getStepParserResult(string: CharSequence): T {
        return rule.getStepParserResult(string)
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        string.codePoints()
        return rule.parseStep(seek, string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return noParse(seek, string)
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        return rule.noParseStep(seek, string)
    }

    override fun repeat(): Rule<*> {
        return rule.repeat()
    }
}