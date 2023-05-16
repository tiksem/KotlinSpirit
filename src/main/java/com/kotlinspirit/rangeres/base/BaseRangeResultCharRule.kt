package com.kotlinspirit.rangeres.base

import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.rangeres.core.RangeResultRuleCore

internal abstract class BaseRangeResultCharRule(
    val core: RangeResultRuleCore<Char>,
    name: String?
) : CharRule(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return core.parse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        core.parseWithResult(seek, string, result)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return core.hasMatch(seek, string)
    }

    override fun ignoreCallbacks(): Rule<Char> {
        return core.ignoreCallbacks()
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    final override val defaultDebugName: String
        get() = core.debugName

    override fun isThreadSafe(): Boolean {
        return false
    }

    override fun getRange(out: ParseRange): CharRule {
        throw IllegalStateException("Double getRange|getRangeResult call")
    }

    override fun getRange(callback: (ParseRange) -> Unit): CharRule {
        throw IllegalStateException("Double getRange|getRangeResult call")
    }

    override fun getRangeResult(out: ParseRangeResult<Char>): CharRule {
        throw IllegalStateException("Double getRange|getRangeResult call")
    }

    override fun getRangeResult(callback: (ParseRangeResult<Char>) -> Unit): CharRule {
        throw IllegalStateException("Double getRange|getRangeResult call")
    }
}