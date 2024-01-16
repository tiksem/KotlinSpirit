package com.kotlinspirit.group

import com.kotlinspirit.core.BaseRuleWithResult
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult
import java.lang.UnsupportedOperationException

class GroupResultRule<T : Any>(name: String?, private val rule: Rule<T>) : Rule<T>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return rule.parse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return rule.reverseParse(seek, string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.reverseParseWithResult(seek, string, result)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.reverseHasMatch(seek, string)
    }

    override fun repeat(): Rule<*> {
        throw UnsupportedOperationException("result group couldn't be repeated")
    }

    override fun repeat(range: IntRange): Rule<*> {
        return repeat()
    }

    override fun repeat(count: Int): Rule<*> {
        return repeat()
    }

    override fun unaryPlus(): Rule<*> {
        return repeat()
    }

    override fun invoke(callback: (T) -> Unit): BaseRuleWithResult<T> {
        throw UnsupportedOperationException("result group couldn't have callback, add a callback to group itself")
    }

    override fun getRange(out: ParseRange): Rule<T> {
        throw UnsupportedOperationException("result group couldn't have getRange, add getRange to group itself")
    }

    override fun getRange(callback: (ParseRange) -> Unit): Rule<T> {
        throw UnsupportedOperationException("result group couldn't have getRange, add getRange to group itself")
    }

    override fun getRangeResult(out: ParseRangeResult<T>): Rule<T> {
        throw UnsupportedOperationException("result group couldn't have getRangeResult, add getRangeResult to group itself")
    }

    override fun getRangeResult(callback: (ParseRangeResult<T>) -> Unit): Rule<T> {
        throw UnsupportedOperationException("result group couldn't have getRangeResult, add getRangeResult to group itself")
    }

    override fun clone(): Rule<T> {
        return GroupResultRule(name, rule.clone())
    }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun name(name: String): Rule<T> {
        return GroupResultRule(name, rule)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "result(${rule.debugName})"
}