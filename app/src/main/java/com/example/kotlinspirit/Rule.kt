package com.example.kotlinspirit

class ParseResult<T> {
    var data: T? = null
    var errorCodeOrSeek: Int = StepCode.MAY_COMPLETE
}

interface Rule<T : Any> {
    fun parse(seek: Int, string: CharSequence): Int {
        resetStep()
        while (true) {
            val stepResult = parseStep(seek, string)
            val stepCode = stepResult.getStepCode()
            if (stepCode.isErrorOrComplete()) {
                return stepResult.toSeekOrError()
            }
        }
    }

    fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val parseResult = parse(seek, string)
        result.errorCodeOrSeek = parseResult
        if (parseResult >= 0) {
            result.data = getStepParserResult(string)
        }
    }

    fun hasMatch(seek: Int, string: CharSequence): Boolean {
        resetStep()
        while (true) {
            val code = parseStep(seek, string).getStepCode()
            if (code.isError()) {
                return false
            } else if (code.canComplete()) {
                return true
            }
        }
    }

    fun resetStep()
    fun getStepParserResult(string: CharSequence): T
    fun parseStep(seek: Int, string: CharSequence): Long

    fun resetNoStep() {
        resetStep()
    }

    fun noParse(seek: Int, string: CharSequence): Int
    fun noParseStep(seek: Int, string: CharSequence): Long

    operator fun not(): Rule<*> {
        return NoRule(this)
    }

    infix fun or(anotherRule: Rule<Any>): AnyOrRule {
        return AnyOrRule(this as Rule<Any>, anotherRule)
    }

    infix fun or(anotherRule: Rule<T>): OrRule<T> {
        return OrRule(this, anotherRule)
    }

    operator fun plus(rule: Rule<*>): SequenceRule {
        return SequenceRule(this, rule)
    }

    operator fun minus(rule: Rule<*>): DiffRule<T> {
        return DiffRule(main = this, diff = rule)
    }

    fun repeat(): Rule<List<T>> {
        return ZeroOrMoreRule(this)
    }

    operator fun invoke(callback: (T) -> Unit): RuleWithResult<T> {
        return RuleWithResult(this, callback)
    }

    operator fun rem(divider: Rule<*>): SplitRule<T> {
        return SplitRule(rule = this, divider = divider)
    }

    fun notifyParseStepComplete(string: CharSequence) {}

    fun clone(): Rule<T>

    fun parseWithResultOrThrow(string: CharSequence): T {
        val result = ParseResult<T>()
        parseWithResult(0, string, result)
        if (result.errorCodeOrSeek < 0) {
            throw ParseException(
                -result.errorCodeOrSeek
            )
        } else {
            return result.data!!
        }
    }
}