package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.str

private val DEFAULT_STEP_RESULT = createStepResult(
    seek = 0,
    stepCode = StepCode.COMPLETE
)

class ParseResult<T> {
    var data: T? = null
    var stepResult: Long = DEFAULT_STEP_RESULT
}

interface Rule<T : Any> {
    fun parse(seek: Int, string: CharSequence): Long {
        resetStep()
        while (true) {
            val stepResult = parseStep(seek, string)
            val stepCode = stepResult.getStepCode()
            if (stepCode.isErrorOrComplete()) {
                return stepResult
            }
        }
    }

    fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val parseResult = parse(seek, string)
        result.stepResult = parseResult
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

    operator fun plus(char: Char): SequenceRule {
        val c = this
        return SequenceRule(c, CharPredicateRule {
            it == char
        })
    }

    operator fun plus(string: String): SequenceRule {
        val c = this
        return SequenceRule(c, ExactStringRule(string))
    }

    operator fun minus(rule: Rule<*>): DiffRule<T> {
        return DiffRule(main = this, diff = rule)
    }

    fun repeat(): Rule<*>
    fun repeat(range: IntRange): Rule<*>

    operator fun invoke(callback: (T) -> Unit): BaseRuleWithResult<T>

    operator fun rem(divider: Rule<*>): SplitRule<T> {
        return split(divider = divider, range = 1..Int.MAX_VALUE)
    }

    fun split(divider: Rule<*>, range: IntRange): SplitRule<T> {
        return SplitRule(r = this, divider = divider, range = range)
    }

    fun split(divider: Rule<*>, times: Int): SplitRule<T> {
        return split(divider = divider, range = times..times)
    }

    fun split(divider: Char, range: IntRange): SplitRule<T> {
        return split(char(divider), range)
    }

    fun split(divider: String, range: IntRange): SplitRule<T> {
        return split(str(divider), range)
    }

    fun split(divider: Char, times: Int): SplitRule<T> {
        return split(char(divider), times)
    }

    fun split(divider: String, times: Int): SplitRule<T> {
        return split(str(divider), times)
    }

    operator fun rem(divider: Char): SplitRule<T> {
        return rem(char(divider))
    }

    operator fun rem(divider: String): SplitRule<T> {
        return rem(str(divider))
    }

    fun notifyParseStepComplete(string: CharSequence) {}

    fun clone(): Rule<T>

    fun parseWithResultOrThrow(string: CharSequence): T {
        val result = ParseResult<T>()
        parseWithResult(0, string, result)
        val stepResult = result.stepResult
        if (stepResult.getStepCode().isError()) {
            throw ParseException(stepResult, string)
        } else {
            return result.data!!
        }
    }

    fun matchOrThrow(string: CharSequence) {
        val result = parse(0, string)
        if (result.getStepCode().isError()) {
            throw ParseException(result, string)
        }
    }

    fun match(string: CharSequence): Boolean {
        return parse(0, string).getStepCode() == StepCode.COMPLETE
    }

    fun asStringRule(): StringRuleWrapper {
        return StringRuleWrapper(this)
    }
}