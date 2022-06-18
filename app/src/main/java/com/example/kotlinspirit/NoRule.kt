package com.example.kotlinspirit

class NoRule(
    private val rule: Rule<*>
) : BaseRule<CharSequence>() {
    private var stepSeekBegin = -1
    private var stepEndSeek = -1

    override fun parse(seek: Int, string: CharSequence): Int {
        return rule.noParse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        result.errorCodeOrSeek = parse(seek, string)
        if (result.errorCodeOrSeek >= 0) {
            result.data = string.subSequence(seek, result.errorCodeOrSeek)
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return !rule.hasMatch(seek, string)
    }

    override fun resetStep() {
        rule.resetNoStep()
        stepSeekBegin = -1
        stepEndSeek = -1
    }

    override fun resetNoStep() {
        rule.resetStep()
    }

    override fun getStepParserResult(string: CharSequence): CharSequence {
        if (stepSeekBegin < 0 || stepEndSeek < 0) {
            throw IllegalStateException("NoRule doesn't contain result")
        }

        return string.subSequence(stepSeekBegin, stepEndSeek)
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        if (stepSeekBegin < 0) {
            stepSeekBegin = seek
        }

        return rule.noParseStep(seek, string).also {
            stepEndSeek = it.getSeek()
        }
    }

    override fun clone(): NoRule {
        return NoRule(rule = rule.clone())
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return rule.parse(seek, string)
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        return rule.parseStep(seek, string)
    }
}