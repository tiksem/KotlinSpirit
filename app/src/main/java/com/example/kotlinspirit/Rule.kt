package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.str

private val DEFAULT_STEP_RESULT = createStepResult(
    seek = 0,
    parseCode = ParseCode.COMPLETE
)

class ParseSeekResult(
    private val stepResult: Long
) {
    val errorCode: Int
        get() {
            val stepCode = stepResult.getParseCode()
            return if (stepCode.isError()) {
                stepCode
            } else {
                -1
            }
        }

    val isError: Boolean
        get() = stepResult.getParseCode().isError()

    val seek: Int
        get() = stepResult.getSeek()
}

class ParseResult<T> {
    var data: T? = null
        internal set
    internal var stepResult: Long = DEFAULT_STEP_RESULT

    val errorCode: Int
        get() {
            val stepCode = stepResult.getParseCode()
            return if (stepCode.isError()) {
                stepCode
            } else {
                -1
            }
        }

    val isError: Boolean
        get() = stepResult.getParseCode().isError()

    val seek: Int
        get() = stepResult.getSeek()
}

abstract class Rule<T : Any> {
    internal var threadId = Thread.currentThread().id
        private set

    internal abstract fun parse(seek: Int, string: CharSequence): Long
    internal abstract fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>)

    internal abstract fun hasMatch(seek: Int, string: CharSequence): Boolean
    internal abstract fun noParse(seek: Int, string: CharSequence): Int

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

    operator fun minus(string: String): DiffRule<T> {
        return DiffRule(main = this, diff = str(string))
    }

    operator fun minus(ch: Char): DiffRule<T> {
        return DiffRule(main = this, diff = char(ch))
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

    abstract fun clone(): Rule<T>

    fun asStringRule(): StringRuleWrapper {
        return StringRuleWrapper(this.clone())
    }

    fun optional(): OptionalRule {
        return OptionalRule(this.clone())
    }

    fun parseGetResultOrThrow(string: CharSequence): T {
        val result = ParseResult<T>()
        parseWithResult(0, string, result)
        val stepResult = result.stepResult
        if (stepResult.getParseCode().isError()) {
            throw ParseException(stepResult, string)
        } else {
            return result.data!!
        }
    }

    fun parseOrThrow(string: CharSequence): Int {
        val result = parse(0, string)
        if (result.getParseCode().isError()) {
            throw ParseException(
                result, string
            )
        }

        return result.getSeek()
    }

    fun tryParse(string: CharSequence): Int? {
        val result = parse(0, string)
        if (result.getParseCode().isError()) {
            return null
        }

        return result.getSeek()
    }

    fun parseWithResult(string: CharSequence): ParseResult<T> {
        val result = ParseResult<T>()
        parseWithResult(0, string, result)
        return result
    }

    fun parse(string: CharSequence): ParseSeekResult {
        val result = parse(0, string)
        return ParseSeekResult(stepResult = result)
    }

    fun matchOrThrow(string: CharSequence) {
        val result = parse(0, string)
        if (result.getParseCode().isError()) {
            throw ParseException(result, string)
        }

        val seek = result.getSeek()
        if (seek != string.length) {
            throw ParseException(
                result = createStepResult(
                    seek = seek,
                    parseCode = ParseCode.WHOLE_STRING_DOES_NOT_MATCH
                ),
                string = string
            )
        }
    }

    fun matches(string: CharSequence): Boolean {
        val result = parse(0, string)
        return result.getParseCode().isNotError() && result.getSeek() == string.length
    }

    fun matchesAtBeginning(string: CharSequence): Boolean {
        return hasMatch(0, string)
    }
}