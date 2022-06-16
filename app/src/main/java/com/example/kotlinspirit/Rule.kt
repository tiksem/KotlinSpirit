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

    infix fun or(anotherRule: Rule<*>): AnyOrRule {
        return AnyOrRule(this as Rule<Any>, anotherRule as Rule<Any>)
    }

    infix fun or(anotherRule: Rule<T>): OrRule<T> {
        val c = this
        return OrRule(c, anotherRule)
    }

    operator fun plus(rule: Rule<*>): SequenceRule {
        val c = this
        return SequenceRule(c, rule)
    }

    operator fun minus(rule: Rule<*>): DiffRule<T> {
        return DiffRule(main = this, diff = rule)
    }

    fun repeat(): Rule<List<T>> {
        return ZeroOrMoreRule(this)
    }

    operator fun invoke(callback: (T) -> Unit): RuleWithResult<T> {
        return RuleWithResult(this.clone(), callback)
    }

    operator fun rem(divider: Rule<*>): SplitRule<T> {
        return SplitRule(r = this, divider = divider)
    }

    operator fun rem(divider: Char): SplitRule<T> {
        return SplitRule(r = this, divider = Rules.char(divider))
    }

    operator fun rem(divider: String): SplitRule<T> {
        return SplitRule(r = this, divider = Rules.str(divider))
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

    fun matchOrThrow(string: CharSequence) {
        val result = parse(0, string)
        if (result < 0) {
            throw ParseException(-result)
        }
    }
}