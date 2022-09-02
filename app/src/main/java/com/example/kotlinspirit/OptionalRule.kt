package com.example.kotlinspirit

class OptionalRule<T : Any>(
    private val rule: Rule<T>
) : RuleWithDefaultRepeat<T>() {

    override fun parse(seek: Int, string: CharSequence): Long {
        return createStepResult(
            seek = rule.parse(seek, string).getSeek(),
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<T>
    ) {
        result.data = null
        val ruleRes = rule.parse(seek, string)
        result.parseResult = createStepResult(
            seek = ruleRes.getSeek(),
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return -seek - 1
    }

    override fun clone(): OptionalRule<T> {
        return OptionalRule(rule)
    }
}