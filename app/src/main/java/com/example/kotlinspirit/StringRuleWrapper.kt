package com.example.kotlinspirit

class StringRuleWrapper(
    private val rule: Rule<*>
) : BaseRule<CharSequence>() {
    private var stepSeekBegin = -1
    private var stepEndSeek = -1

    override fun invoke(callback: (CharSequence) -> Unit): RuleWithResult<CharSequence> {
        return RuleWithResult(this.clone(), callback)
    }

    override fun clone(): StringRuleWrapper {
        return StringRuleWrapper(rule.clone())
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        return rule.parse(seek, string)
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<CharSequence>
    ) {
        val parseResult = rule.parse(seek, string)
        result.stepResult = parseResult
        if (parseResult.getStepCode().isNotError()) {
            result.data = string.subSequence(seek, parseResult.getSeek())
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
    }

    override fun resetStep() {
        stepSeekBegin = -1
        stepEndSeek = -1
        rule.resetStep()
    }

    override fun getStepParserResult(string: CharSequence): CharSequence {
        if (stepEndSeek < 0 || stepSeekBegin < 0) {
            return ""
        }

        return string.subSequence(stepSeekBegin, stepEndSeek)
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        if (stepSeekBegin < 0) {
            stepSeekBegin = seek
        }

        return rule.parseStep(seek, string).also {
            stepEndSeek = it.getSeek()
        }
    }

    override fun resetNoStep() {
        rule.resetNoStep()
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return rule.noParse(seek, string)
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        return rule.noParseStep(seek, string)
    }
}