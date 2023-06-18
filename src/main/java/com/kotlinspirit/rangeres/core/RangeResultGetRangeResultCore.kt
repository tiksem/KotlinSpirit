package com.kotlinspirit.rangeres.core

import com.kotlinspirit.core.*
import com.kotlinspirit.rangeres.ParseRangeResult

internal class RangeResultGetRangeResultCore<T : Any>(
    rule: Rule<T>,
    private val out: ParseRangeResult<T>
) : RangeResultRuleCore<T>(rule) {
    private val parseResult = ParseResult<T>()

    override fun parse(seek: Int, string: CharSequence): Long {
        parseWithResult(seek, string, parseResult)
        return parseResult.parseResult
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (!result.isError) {
            out.startSeek = seek
            out.endSeek = result.endSeek
            out.data = result.data
        } else {
            out.startSeek = -1
            out.endSeek = -1
            out.data = null
        }
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        reverseParseWithResult(seek, string, parseResult)
        return parseResult.parseResult
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.reverseParseWithResult(seek, string, result)
        if (!result.isError) {
            out.startSeek = result.endSeek + 1
            out.endSeek = seek + 1
            out.data = result.data
        } else {
            out.startSeek = -1
            out.endSeek = -1
            out.data = null
        }
    }

    override val debugName: String
        get() = "getResultRange"
}