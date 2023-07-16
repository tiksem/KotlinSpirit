package com.kotlinspirit.rangeres.core

import com.kotlinspirit.core.*
import com.kotlinspirit.rangeres.ParseRange

internal class RangeResultRuleCallbacksCore<T : Any>(
    rule: Rule<T>,
    private val callback: (ParseRange) -> Unit
) : RangeResultRuleCore<T>(rule) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return rule.parse(seek, string).also {
            if (it.isComplete) {
                callback(ParseRange(seek, it.seek))
            }
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (!result.isError) {
            callback(ParseRange(seek, result.endSeek))
        }
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return rule.reverseParse(seek, string).also {
            if (it.isComplete) {
                callback(ParseRange(it.seek + 1, seek + 1))
            }
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.reverseParseWithResult(seek, string, result)
        if (!result.isError) {
            callback(ParseRange(result.endSeek + 1, seek + 1))
        }
    }

    override val debugName: String
        get() = "getRange"
}