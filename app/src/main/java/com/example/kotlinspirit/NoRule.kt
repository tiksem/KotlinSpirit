package com.example.kotlinspirit

class NoRule(
    private val rule: Rule<*>
) : RuleWithDefaultRepeat<CharSequence>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        return rule.noParse(seek, string).let {
            if (it < 0) {
                return createStepResult(
                    seek = -it + 1,
                    parseCode = ParseCode.NO_FAILED
                )
            } else {
                createComplete(it)
            }
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        val parseResult = rule.noParse(seek, string)
        if (parseResult >= 0) {
            result.data = string.subSequence(seek, parseResult)
            result.parseResult = createStepResult(
                seek = parseResult,
                parseCode = ParseCode.COMPLETE
            )
        } else {
            result.parseResult = createStepResult(
                seek = -parseResult + 1,
                parseCode = ParseCode.NO_FAILED
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return !rule.hasMatch(seek, string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return rule.parse(seek, string).let {
            if (it.getParseCode().isNotError()) {
                it.getSeek()
            } else {
                -it.getSeek() - 1
            }
        }
    }

    override fun clone(): NoRule {
        return NoRule(rule.clone())
    }
}