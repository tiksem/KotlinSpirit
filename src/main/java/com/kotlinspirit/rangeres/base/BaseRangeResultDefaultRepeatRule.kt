package com.kotlinspirit.rangeres.base

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.rangeres.simple.RangeResultRuleDefaultRepeat
import com.kotlinspirit.rangeres.core.RangeResultRuleCore
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

internal abstract class BaseRangeResultDefaultRepeatRule<T : Any> internal constructor(
    internal val core: RangeResultRuleCore<T>
) : RuleWithDefaultRepeat<T>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        return core.parse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        core.parseWithResult(seek, string, result)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return core.hasMatch(seek, string)
    }

    override fun ignoreCallbacks(): Rule<T> {
        return core.ignoreCallbacks()
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun isThreadSafe(): Boolean {
        return false
    }

    override fun getRange(out: ParseRange): RangeResultRuleDefaultRepeat<T> {
        throw IllegalStateException("Double getRange|getRangeResult call")
    }

    override fun getRange(callback: (ParseRange) -> Unit): RuleWithDefaultRepeat<T> {
        throw IllegalStateException("Double getRange|getRangeResult call")
    }

    override fun getRangeResult(out: ParseRangeResult<T>): RuleWithDefaultRepeat<T> {
        throw IllegalStateException("Double getRange|getRangeResult call")
    }

    override fun getRangeResult(callback: (ParseRangeResult<T>) -> Unit): RuleWithDefaultRepeat<T> {
        throw IllegalStateException("Double getRange|getRangeResult call")
    }
}