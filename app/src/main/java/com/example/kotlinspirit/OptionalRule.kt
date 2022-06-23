package com.example.kotlinspirit

class OptionalRule(
    private val rule: Rule<*>
) : RuleWithDefaultRepeat<CharSequence>() {
    private var stepBeginSeek = -1
    private var stepEndSeek = -1

    override fun parse(seek: Int, string: CharSequence): Long {
        return createStepResult(
            seek = rule.parse(seek, string).getSeek(),
            stepCode = StepCode.COMPLETE
        )
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<CharSequence>
    ) {
        val res = rule.parse(seek, string)
        val endSeek = res.getSeek()
        result.stepResult = createStepResult(
            seek = endSeek,
            stepCode = StepCode.COMPLETE
        )
        result.data = string.subSequence(seek, endSeek)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun resetStep() {
        rule.resetStep()
        stepBeginSeek = -1
        stepEndSeek = -1
    }

    override fun getStepParserResult(string: CharSequence): CharSequence {
        return string.subSequence(stepBeginSeek, stepEndSeek)
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        if (stepBeginSeek < 0) {
            stepBeginSeek = seek
        }

        return rule.parseStep(seek, string).let {
            val stepCode = it.getStepCode()
            val resultStepCode = if (stepCode.isError()) {
                it.getStepCode()
            } else {
                StepCode.COMPLETE
            }
            stepEndSeek = it.getSeek()
            createStepResult(
                seek = stepEndSeek,
                stepCode = resultStepCode
            )
        }
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return -seek
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        return createStepResult(
            seek = seek,
            stepCode = StepCode.NO_FAILED
        )
    }

    override fun clone(): OptionalRule {
        return OptionalRule(rule.clone())
    }
}