package com.kotlinspirit.result

import com.kotlinspirit.char.ExactCharRule
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.rangeres.callbacks.RangeResultCallbacksRuleDefaultRepeat
import com.kotlinspirit.rangeres.result.RangeResultRuleCallbacksResultDefaultRepeat
import com.kotlinspirit.rangeres.result.RangeResultRuleResultDefaultRepeat
import com.kotlinspirit.rangeres.simple.RangeResultRuleDefaultRepeat
import com.kotlinspirit.repeat.RepeatRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.repeat.RuleWithDefaultRepeatResult
import com.kotlinspirit.str.ExactStringRule

class ResultSequenceRule<T : Any>(
    private val a: Rule<*>,
    private val b: Rule<*>,
    private val aIsResultRule: Boolean,
    name: String? = null
) : Rule<T>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        val aResult = a.parse(seek, string)
        if (aResult.isError) {
            return aResult
        }

        return b.parse(aResult.seek, string)
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<T>
    ) {
        val aResult = if (aIsResultRule) {
            (a as Rule<T>).parseWithResult(seek, string, result)
            result.parseResult
        } else {
            a.parse(seek, string)
        }
        if (aResult.isError) {
            result.parseResult = aResult
            result.data = null
            return
        }

        val bResult = if (aIsResultRule) {
            b.parse(aResult.seek, string)
        } else {
            (b as Rule<T>).parseWithResult(aResult.seek, string, result)
            result.parseResult
        }
        result.parseResult = bResult
        if (bResult.isError) {
            result.data = null
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val aResult = a.parse(seek, string)
        return if (aResult.isError) {
            false
        } else {
            b.hasMatch(aResult.seek, string)
        }
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        val bResult = b.reverseParse(seek, string)
        if (bResult.isError) {
            return bResult
        }

        return a.reverseParse(bResult.seek, string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val aResult = if (aIsResultRule) {
            (a as Rule<T>).reverseParseWithResult(seek, string, result)
            result.parseResult
        } else {
            a.parse(seek, string)
        }
        if (aResult.isError) {
            result.parseResult = aResult
            result.data = null
            return
        }

        val bResult = if (aIsResultRule) {
            b.parse(seek, string)
        } else {
            (b as Rule<T>).reverseParseWithResult(seek, string, result)
            result.parseResult
        }
        result.parseResult = bResult
        if (bResult.isError) {
            result.data = null
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        val bResult = b.reverseParse(seek, string)
        return if (bResult.isError) {
            false
        } else {
            a.reverseHasMatch(bResult.seek, string)
        }
    }

    override fun plus(rule: Rule<*>): ResultSequenceRule<T> {
        return ResultSequenceRule(a = this, b = rule, aIsResultRule = true)
    }

    override fun plus(char: Char): Rule<*> {
        return this + ExactCharRule(char)
    }

    override fun plus(string: String): Rule<*> {
        return this + ExactStringRule(false, string)
    }

    override fun clone(): ResultSequenceRule<T> {
        return ResultSequenceRule<T>(a.clone(), b.clone(), aIsResultRule, name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override val defaultDebugName: String
        get() = if (aIsResultRule) {
            "r(${a.wrappedName})+${b.wrappedName}"
        } else {
            "${a.wrappedName}+r(${b.wrappedName})"
        }

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = ResultSequenceRule(a.debug(engine), b.debug(engine), aIsResultRule, name),
            engine = engine
        )
    }

    override fun name(name: String): ResultSequenceRule<T> {
        return ResultSequenceRule(a, b, aIsResultRule, name)
    }

    override fun isThreadSafe(): Boolean {
        return a.isThreadSafe() && b.isThreadSafe()
    }

    override fun repeat(): Rule<List<T>> {
        return RepeatRule(rule = this, range = 0..Int.MAX_VALUE)
    }

    override fun repeat(range: IntRange): Rule<List<T>> {
        return RepeatRule(this, range)
    }

    override fun repeat(count: Int): Rule<List<T>> {
        return repeat(range = count..count)
    }

    override fun unaryPlus(): Rule<List<T>> {
        return RepeatRule(rule = this, range = 1..Int.MAX_VALUE)
    }

    override fun invoke(callback: (T) -> Unit): RuleWithDefaultRepeatResult<T> {
        return RuleWithDefaultRepeatResult(this, callback)
    }

    override fun getRange(out: ParseRange): RuleWithDefaultRepeat<T> {
        return RangeResultRuleDefaultRepeat(this, out)
    }

    override fun getRange(callback: (ParseRange) -> Unit): RuleWithDefaultRepeat<T> {
        return RangeResultCallbacksRuleDefaultRepeat(this, callback)
    }

    override fun getRangeResult(out: ParseRangeResult<T>): RuleWithDefaultRepeat<T> {
        return RangeResultRuleResultDefaultRepeat(this, out)
    }

    override fun getRangeResult(callback: (ParseRangeResult<T>) -> Unit): RuleWithDefaultRepeat<T> {
        return RangeResultRuleCallbacksResultDefaultRepeat(this, callback)
    }
}