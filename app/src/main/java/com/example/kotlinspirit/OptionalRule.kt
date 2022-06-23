package com.example.kotlinspirit

class OptionalRule(
    private val rule: Rule<*>
) : RuleWithDefaultRepeat<CharSequence>() {
    private var stepBeginSeek = -1
    private var stepEndSeek = -1

    override fun parse(seek: Int, string: CharSequence): Long {
        return createStepResult(
            seek = rule.parse(seek, string).getSeek(),
            parseCode = ParseCode.COMPLETE
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
            parseCode = ParseCode.COMPLETE
        )
        result.data = string.subSequence(seek, endSeek)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return -seek
    }

    override fun clone(): OptionalRule {
        return OptionalRule(rule.clone())
    }
}