package com.kotlinspirit.core

import com.kotlinspirit.*
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.str
import com.kotlinspirit.char.ExactCharRule
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.ext.containsAny
import com.kotlinspirit.ext.quoteIf
import com.kotlinspirit.expressive.*
import com.kotlinspirit.quoted.QuotedRule
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

    val seek: Int
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

    val seek: Int
        get() = parseResult.getSeek()
}

abstract class Rule<T : Any> {
    internal abstract fun parse(seek: Int, string: CharSequence): Long
    internal abstract fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>)
    internal abstract fun hasMatch(seek: Int, string: CharSequence): Boolean

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

    open infix fun or(string: String): Rule<*> {
        return AnyOrRule(this as Rule<Any>, str(string) as Rule<Any>)
    }

    operator fun plus(rule: Rule<*>): SequenceRule {
        val c = this
        return SequenceRule(c, rule)
    }

    operator fun plus(char: Char): SequenceRule {
        return SequenceRule(this, ExactCharRule(char))
    }

    operator fun plus(string: String): SequenceRule {
        val c = this
        return SequenceRule(c, ExactStringRule(string))
    }

    open operator fun minus(rule: Rule<*>): Rule<T> {
        return DiffRuleWithDefaultRepeat(main = this, diff = rule)
    }

    open operator fun minus(string: String): Rule<T> {
        return DiffRuleWithDefaultRepeat(main = this, diff = str(string))
    }

    open operator fun minus(ch: Char): Rule<T> {
        return DiffRuleWithDefaultRepeat(main = this, diff = char(ch))
    }

    fun expect(other: Rule<*>): ExpectationRule<T> {
        return ExpectationRule(this, other)
    }

    fun expect(string: String): ExpectationRule<T> {
        return ExpectationRule(this, str(string))
    }

    fun expect(char: Char): ExpectationRule<T> {
        return ExpectationRule(this, ExactCharRule(char))
    }

    abstract fun repeat(): Rule<*>
    abstract fun repeat(range: IntRange): Rule<*>
    abstract operator fun unaryPlus(): Rule<*>

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

    abstract val debugNameShouldBeWrapped: Boolean

    fun compile(): Parser<T> {
        val parser = if (isThreadSafe()) {
            RegularParser(originalRule = this)
        } else {
            ThreadSafeParser(originRule = this)
        }
        return if (this is DebugRule) {
            DebugParser(parser)
        } else {
            parser
        }
    }

    internal abstract fun isThreadSafe(): Boolean

    abstract fun debug(name: String? = null): Rule<T>
    internal fun internalDebug(name: String? = null): Rule<T> {
        return if (this is DebugRule) {
            if (name == null || this.name == name) {
                this
            } else {
                debug(name)
            }
        } else {
            debug(name)
        }
    }

    internal val debugName: String
        get() = (this as? DebugRule)?.name ?: throw RuntimeException()

    internal val debugNameWrapIfNeed: String
        get() = (this as? DebugRule)?.name?.quoteIf('(', ')',
            debugNameShouldBeWrapped && debugName.containsAny("() \n\t")
        ) ?: throw RuntimeException()
}