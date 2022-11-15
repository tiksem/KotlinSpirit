package com.kotlinspirit.rangeres.core

import com.kotlinspirit.core.*
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isNotError
import com.kotlinspirit.rangeres.ParseRange

internal class RangeResultRuleCallbacksCore<T : Any>(
    rule: Rule<T>,
    private val callback: (ParseRange) -> Unit
) : RangeResultRuleCore<T>(rule) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return rule.parse(seek, string).also {
            if (it.getParseCode().isNotError()) {
                callback(ParseRange(seek, it.getSeek()))
            }
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (!result.isError) {
            callback(ParseRange(seek, result.endSeek))
        }
    }
}