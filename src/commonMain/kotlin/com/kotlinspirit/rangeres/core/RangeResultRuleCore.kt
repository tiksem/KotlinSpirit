package com.kotlinspirit.rangeres.core

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule

internal abstract class RangeResultRuleCore<T : Any>(
    val rule: Rule<T>
) {
    internal abstract fun parse(seek: Int, string: CharSequence): ParseSeekResult
    internal abstract fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>)
    internal abstract fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult
    internal abstract fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>)

    abstract val debugName: String

    fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
    }

    fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.reverseHasMatch(seek, string)
    }
}