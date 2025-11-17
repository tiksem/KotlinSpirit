package com.kotlinspirit.result

import com.kotlinspirit.char.ExactCharRule
import com.kotlinspirit.core.BaseRuleWithResult
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.str.ExactStringRule

class ResultInSequenceRuleWrapper<T : Any>(
    private val wrappedRule: Rule<T>,
    name: String? = null
) : Rule<T>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return wrappedRule.parse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        return wrappedRule.parseWithResult(seek, string, result)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return wrappedRule.hasMatch(seek, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return wrappedRule.reverseParse(seek, string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        wrappedRule.reverseParseWithResult(seek, string, result)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return wrappedRule.reverseHasMatch(seek, string)
    }

    override fun plus(rule: Rule<*>): ResultSequenceRule<T> {
        return ResultSequenceRule(a = this, b = rule, aIsResultRule = true)
    }

    override fun plus(char: Char): ResultSequenceRule<T> {
        return this + ExactCharRule(char)
    }

    override fun plus(string: String): ResultSequenceRule<T> {
        return this + ExactStringRule(false, string)
    }

    override fun repeat(): Rule<*> {
        throw UnsupportedOperationException("asResult could be used only inside sequence(+)")
    }

    override fun repeat(range: IntRange): Rule<*> {
        throw UnsupportedOperationException("asResult could be used only inside sequence(+)")
    }

    override fun repeat(count: Int): Rule<*> {
        throw UnsupportedOperationException("asResult could be used only inside sequence(+)")
    }

    override fun unaryPlus(): Rule<*> {
        throw UnsupportedOperationException("asResult could be used only inside sequence(+)")
    }

    override fun invoke(callback: (T) -> Unit): BaseRuleWithResult<T> {
        throw UnsupportedOperationException("asResult could be used only inside sequence(+)")
    }

    override fun getRange(out: ParseRange): Rule<T> {
        throw UnsupportedOperationException("asResult could be used only inside sequence(+)")
    }

    override fun getRange(callback: (ParseRange) -> Unit): Rule<T> {
        throw UnsupportedOperationException("asResult could be used only inside sequence(+)")
    }

    override fun getRangeResult(out: ParseRangeResult<T>): Rule<T> {
        throw UnsupportedOperationException("asResult could be used only inside sequence(+)")
    }

    override fun getRangeResult(callback: (ParseRangeResult<T>) -> Unit): Rule<T> {
        throw UnsupportedOperationException("asResult could be used only inside sequence(+)")
    }

    override fun clone(): ResultInSequenceRuleWrapper<T> {
        return ResultInSequenceRuleWrapper(wrappedRule.clone(), name)
    }

    override fun isThreadSafe(): Boolean {
        return wrappedRule.isThreadSafe()
    }

    override fun name(name: String): ResultInSequenceRuleWrapper<T> {
        return ResultInSequenceRuleWrapper(wrappedRule, name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = wrappedRule.debugNameShouldBeWrapped

    override val defaultDebugName: String
        get() = wrappedRule.defaultDebugName
}