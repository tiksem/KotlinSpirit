package com.example.kotlinspirit

class StringRuleWrapper(
    private val rule: Rule<*>
) : RuleWithDefaultRepeat<CharSequence>() {
    private var stepSeekBegin = -1
    private var stepEndSeek = -1

    override fun invoke(callback: (CharSequence) -> Unit): RuleWithDefaultRepeatResult<CharSequence> {
        return RuleWithDefaultRepeatResult(this.clone(), callback)
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
        if (parseResult.getParseCode().isNotError()) {
            result.data = string.subSequence(seek, parseResult.getSeek())
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return rule.noParse(seek, string)
    }
}