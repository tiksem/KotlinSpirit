package com.kotlinspirit.rangeres.core

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.rangeres.ParseRangeResult

internal class RangeResultRuleResultCallbacksCore<T : Any>(
    rule: Rule<T>,
    private val callback: (ParseRangeResult<T>) -> Unit
) : RangeResultRuleCore<T>(rule) {
    private val parseResult = ParseResult<T>()

    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        parseWithResult(seek, string, parseResult)
        return parseResult.parseResult
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (result.isError) {
            ParseRangeResult<T>(null, -1, -1)
        } else {
            ParseRangeResult(result.data, seek, result.endSeek)
        }.also {
            callback(it)
        }
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        reverseParseWithResult(seek, string, parseResult)
        return parseResult.parseResult
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.reverseParseWithResult(seek, string, result)
        if (result.isError) {
            ParseRangeResult<T>(null, -1, -1)
        } else {
            ParseRangeResult(result.data, result.endSeek + 1, seek + 1)
        }.also {
            callback(it)
        }
    }

    override val debugName: String
        get() = "getResultRange"
}