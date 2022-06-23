package com.example.kotlinspirit

class NoRule(
    private val rule: Rule<*>
) : RuleWithDefaultRepeat<CharSequence>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        return rule.noParse(seek, string).let {
            if (it < 0) {
                return createStepResult(
                    seek = -it,
                    parseCode = ParseCode.NO_FAILED
                )
            } else {
                createComplete(it)
            }
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        result.stepResult = parse(seek, string)
        val stepResult = result.stepResult
        if (!stepResult.getParseCode().isNotError()) {
            result.data = string.subSequence(seek, stepResult.getSeek())
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return !rule.hasMatch(seek, string)
    }

    override fun clone(): NoRule {
        return NoRule(rule = rule.clone())
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return rule.parse(seek, string).let {
            if (it.getParseCode().isNotError()) {
                it.getSeek()
            } else {
                -it.getSeek()
            }
        }
    }
}