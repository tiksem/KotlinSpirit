package com.kotlinspirit.core

import com.kotlinspirit.*
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.str
import com.kotlinspirit.char.ExactCharRule
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.ext.containsAny
import com.kotlinspirit.ext.quoteIf
import com.kotlinspirit.expressive.*
import com.kotlinspirit.str.ExactStringRule

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

    abstract fun clone(): Rule<T>

    abstract val debugNameShouldBeWrapped: Boolean
    open val isGrammar: Boolean
        get() = false

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
        get() = (this as? DebugRule)?.name ?: "error"

    internal val debugNameWrapIfNeed: String
        get() = (this as? DebugRule)?.name?.quoteIf('(', ')',
            debugNameShouldBeWrapped && debugName.containsAny("() \n\t")
        ) ?: "error"
}