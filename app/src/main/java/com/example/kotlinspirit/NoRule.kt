package com.example.kotlinspirit

class NoRule(
    private val rule: Rule<*>
) : Rule<Unit> {
    override fun parse(seek: Int, string: CharSequence): Int {
        return rule.noParse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Unit>) {
        result.errorCodeOrSeek = parse(seek, string)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return !rule.hasMatch(seek, string)
    }

    override fun resetStep() {
        rule.resetNoStep()
    }

    override fun resetNoStep() {
        rule.resetStep()
    }

    override fun getStepParserResult(string: CharSequence): Unit {
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        return rule.noParseStep(seek, string)
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