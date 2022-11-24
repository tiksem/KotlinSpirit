package com.kotlinspirit.rangeres.core

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule

internal abstract class RangeResultRuleCore<T : Any>(
    val rule: Rule<T>
) {
    internal abstract fun parse(seek: Int, string: CharSequence): Long
    internal abstract fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>)
    abstract val debugName: String

    fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
    }

    fun ignoreCallbacks(): Rule<T> {
        return rule.ignoreCallbacks()
    }
}