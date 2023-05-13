package com.kotlinspirit.core

import com.kotlinspirit.*
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.str
import com.kotlinspirit.char.ExactCharRule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.ext.containsAny
import com.kotlinspirit.ext.quoteIf
import com.kotlinspirit.expressive.*
import com.kotlinspirit.ext.quote
import com.kotlinspirit.quoted.QuotedRule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.str.ExactStringRule
import java.lang.RuntimeException

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

    val endSeek: Int
        get() = stepResult.getSeek()
}

class ParseResult<T> {
    var data: T? = null
        internal set
    internal var parseResult: Long = DEFAULT_STEP_RESULT

    val errorCode: Int
        get() {
            val stepCode = parseResult.getParseCode()
            return if (stepCode.isError()) {
                stepCode
            } else {
                -1
            }
        }

    val isError: Boolean
        get() = parseResult.getParseCode().isError()

    val endSeek: Int
        get() = parseResult.getSeek()
}

/**
 * Base class, representing a rule. A rule defines how the text is parsed.
 * @param T the type of the result of the rule.
 */
abstract class Rule<T : Any>(name: String?) {
    var name = name
        private set

    internal abstract fun parse(seek: Int, string: CharSequence): Long
    internal abstract fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>)
    internal abstract fun hasMatch(seek: Int, string: CharSequence): Boolean

    internal inline fun findFirstSuccessfulSeek(string: CharSequence, callback: (Int, Int) -> Unit): Boolean {
        var seek = 0
        val length = string.length
        do {
            val result = parse(seek, string)
            if (result.getParseCode().isNotError()) {
                callback(seek, result.getSeek())
                return true
            }
            val newSeek = result.getSeek()
            if (newSeek == seek) {
                seek++
            } else {
                seek = newSeek
            }
        } while (seek < length)

        return false
    }

    internal inline fun findLastSuccessfulSeek(string: CharSequence, callback: (Int, Int) -> Unit): Boolean {
        var seek = string.length - 1
        do {
            val result = parse(seek, string)
            if (hasMatch(seek, string)) {
                callback(seek, result.getSeek())
                return true
            }
            --seek
        } while (seek >= 0)

        return false
    }

    internal inline fun findFirstSuccessfulResult(string: CharSequence, callback: (Int, ParseResult<T>) -> Unit): Boolean {
        var seek = 0
        val length = string.length
        val result = ParseResult<T>()
        do {
            parseWithResult(seek, string, result)
            if (!result.isError && result.data != null) {
                callback(seek, result)
                return true
            }
            val newSeek = result.endSeek
            if (newSeek == seek) {
                seek++
            } else {
                seek = newSeek
            }
        } while (seek < length)

        return false
    }

    internal inline fun findSuccessfulRanges(string: CharSequence, callback: (Int, Int) -> Unit) {
        var seek = 0
        val length = string.length
        do {
            val result = parse(seek, string)
            if (result.getParseCode().isNotError()) {
                callback(seek, result.getSeek())
            }
            val newSeek = result.getSeek()
            if (newSeek == seek) {
                seek++
            } else {
                seek = newSeek
            }

        } while (seek < length)
    }

    internal inline fun findSuccessfulResults(string: CharSequence, callback: (Int, Int, T) -> Unit) {
        var seek = 0
        val length = string.length
        val result = ParseResult<T>()
        do {
            parseWithResult(seek, string, result)
            if (!result.isError && result.data != null) {
                callback(seek, result.endSeek, result.data!!)
            }
            val newSeek = result.endSeek
            if (newSeek == seek) {
                seek++
            } else {
                seek = newSeek
            }

        } while (seek < length)
    }

    /**
     * Returns a rule, that
     * Matches one character, if it doesn't match this rule.
     * If we are at the end of input, and this rule doesn't match EOF, it outputs '\0' as a result
     */
    open operator fun not(): Rule<*> {
        return NoRule(this)
    }

    /**
     * Returns a rule, that
     * Matches this rule or anotherRule
     */
    infix fun or(anotherRule: Rule<*>): AnyOrRule {
        return AnyOrRule(this as Rule<Any>, anotherRule as Rule<Any>)
    }

    /**
     * Returns a rule, that
     * Matches this rule or anotherRule
     */
    infix fun or(anotherRule: Rule<T>): OrRule<T> {
        val c = this
        return OrRule(c, anotherRule)
    }

    /**
     * Returns a rule, that
     * Matches this rule or the string
     */
    open infix fun or(string: String): Rule<*> {
        return AnyOrRule(this as Rule<Any>, str(string) as Rule<Any>)
    }

    /**
     * Returns a rule, that
     * Matches this rule followed by the passed rule
     */
    operator fun plus(rule: Rule<*>): SequenceRule {
        val c = this
        return SequenceRule(c, rule)
    }

    /**
     * Returns a rule, that
     * Matches this rule followed by the char
     */
    operator fun plus(char: Char): SequenceRule {
        return SequenceRule(this, ExactCharRule(char))
    }

    /**
     * Returns a rule, that
     * Matches this rule followed by the string
     */
    operator fun plus(string: String): SequenceRule {
        val c = this
        return SequenceRule(c, ExactStringRule(string))
    }

    /**
     * Returns a rule, that
     * Matches this rule, but doesn't match the passed rule
     */
    open operator fun minus(rule: Rule<*>): Rule<T> {
        return DiffRuleWithDefaultRepeat(main = this, diff = rule)
    }

    /**
     * Returns a rule, that
     * Matches this rule, but doesn't the string
     */
    open operator fun minus(string: String): Rule<T> {
        return DiffRuleWithDefaultRepeat(main = this, diff = str(string))
    }

    /**
     * Returns a rule, that
     * Matches this rule, but doesn't the char
     */
    open operator fun minus(ch: Char): Rule<T> {
        return DiffRuleWithDefaultRepeat(main = this, diff = char(ch))
    }

    /**
     * Returns a rule, that
     * Matches this rule, only when the other rule goes after this rule
     * Sets the seek after this rule's match at the end of the parsing process
     */
    fun expect(other: Rule<*>): ExpectationRule<T> {
        return ExpectationRule(this, other)
    }

    /**
     * Returns a rule, that
     * Matches this rule, only when the string goes after this rule
     * Sets the seek after this rule's match at the end of the parsing process
     */
    fun expect(string: String): ExpectationRule<T> {
        return ExpectationRule(this, str(string))
    }

    /**
     * Returns a rule, that
     * Matches this rule, only when the char goes after this rule
     * Sets the seek after this rule's match at the end of the parsing process
     */
    fun expect(char: Char): ExpectationRule<T> {
        return ExpectationRule(this, ExactCharRule(char))
    }

    /**
     * Returns a rule, that
     * Matches this rule 0 or more times
     * The returned rule is always successful
     */
    abstract fun repeat(): Rule<*>
    /**
     * Returns a rule, that
     * Matches this rule [range.first,range.last] times
     */
    abstract fun repeat(range: IntRange): Rule<*>
    /**
     * Returns a rule, that
     * Matches this rule 1 or more times
     */
    abstract operator fun unaryPlus(): Rule<*>
    /**
     * Sets a value callback, executed when this rule matches successfully
     */
    abstract operator fun invoke(callback: (T) -> Unit): BaseRuleWithResult<T>
    /**
     * Sets a range hook, filled with startSeek and endSeek, when this rule matches successfully
     */
    abstract fun getRange(out: ParseRange): Rule<T>
    /**
     * Sets a callback, executed when this rule matches successfully
     * @param callback with ParseRange
     */
    abstract fun getRange(callback: (ParseRange) -> Unit): Rule<T>
    /**
     * Sets a hook, filled with startSeek, endSeek, and value when this rule matches successfully
     */
    abstract fun getRangeResult(out: ParseRangeResult<T>): Rule<T>
    /**
     * Sets a callback, executed when this rule matches successfully
     * @param callback with ParseRangeResult<T>
     */
    abstract fun getRangeResult(callback: (ParseRangeResult<T>) -> Unit): Rule<T>

    /**
     * Returns a rule, that
     * Matches this rule splitted by divider rule 1 or more times
     */
    operator fun rem(divider: Rule<*>): SplitRule<T> {
        return split(divider = divider, range = 1..Int.MAX_VALUE)
    }

    /**
     * Returns a rule, that
     * Matches this rule splitted by divider rule [range.first, range.last] times
     */
    fun split(divider: Rule<*>, range: IntRange): SplitRule<T> {
        return SplitRule(r = this, divider = divider, range = range)
    }

    /**
     * Returns a rule, that
     * Matches this rule splitted by divider rule `times` times
     */
    fun split(divider: Rule<*>, times: Int): SplitRule<T> {
        return split(divider = divider, range = times..times)
    }

    /**
     * Returns a rule, that
     * Matches this rule splitted by divider char [range.first, range.last] times
     */
    fun split(divider: Char, range: IntRange): SplitRule<T> {
        return split(char(divider), range)
    }

    /**
     * Returns a rule, that
     * Matches this rule splitted by divider string [range.first, range.last] times
     */
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

    fun asString(): StringRuleWrapper {
        return StringRuleWrapper(this)
    }

    open operator fun unaryMinus(): OptionalRule<T> {
        return OptionalRule(this)
    }

    open fun failIf(predicate: (T) -> Boolean): FailIfRule<T> {
        return FailIfRule(this, predicate)
    }

    open fun quoted(left: Rule<*>, right: Rule<*>): QuotedRule<T> {
        return QuotedRule(this, left, right)
    }

    open fun quoted(rule: Rule<*>): QuotedRule<T> {
        return QuotedRule(this, rule, rule)
    }

    fun quoted(ch: Char): QuotedRule<T> {
        return quoted(char(ch))
    }

    fun quoted(left: Char, right: Char): QuotedRule<T> {
        return quoted(char(left), char(right))
    }

    fun quoted(string: String): QuotedRule<T> {
        return quoted(str(string))
    }

    fun quoted(left: String, right: String): QuotedRule<T> {
        return quoted(str(left), str(right))
    }

    abstract fun ignoreCallbacks(): Rule<T>

    abstract fun clone(): Rule<T>

    fun compile(debug: Boolean = false): Parser<T> {
        return if (debug) {
            DebugParser(originalRule = this)
        } else if (isThreadSafe()) {
            RegularParser(originalRule = this)
        } else {
            ThreadSafeParser(originRule = this)
        }
    }

    abstract fun isThreadSafe(): Boolean

    abstract fun name(name: String): Rule<T>

    internal open fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(rule = this, engine = engine)
    }

    internal abstract val debugNameShouldBeWrapped: Boolean
    internal abstract val defaultDebugName: String

    val wrappedName: String
        get() {
            return if (debugNameShouldBeWrapped && name == null) {
                debugName.quote('(',')')
            } else {
                debugName
            }
        }
    
    val debugName: String
        get() = name ?: defaultDebugName
}