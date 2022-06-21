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

abstract class Rule<T : Any> {
    open fun parse(seek: Int, string: CharSequence): Long {
        resetStep()
        while (true) {
            val stepResult = parseStep(seek, string)
            val stepCode = stepResult.getStepCode()
            if (stepCode.isErrorOrComplete()) {
                return stepResult
            }
        }
    }

    open fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val parseResult = parse(seek, string)
        result.stepResult = parseResult
        if (parseResult >= 0) {
            result.data = getStepParserResult(string)
        }
    }

    open fun hasMatch(seek: Int, string: CharSequence): Boolean {
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

    internal abstract fun resetStep()
    internal abstract fun getStepParserResult(string: CharSequence): T
    internal abstract fun parseStep(seek: Int, string: CharSequence): Long

    internal open fun resetNoStep() {
        resetStep()
    }

    internal abstract fun noParse(seek: Int, string: CharSequence): Int
    internal abstract fun noParseStep(seek: Int, string: CharSequence): Long

    open operator fun not(): Rule<*> {
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

    abstract fun repeat(): Rule<*>
    abstract fun repeat(range: IntRange): Rule<*>

    abstract operator fun invoke(callback: (T) -> Unit): BaseRuleWithResult<T>

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

    internal open fun notifyParseStepComplete(string: CharSequence) {}

    abstract fun clone(): Rule<T>

    fun parseOrThrow(string: CharSequence): T {
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

    fun hasMatch(string: CharSequence): Boolean {
        return hasMatch(0, string)
    }

    fun asStringRule(): StringRuleWrapper {
        return StringRuleWrapper(this)
    }
}