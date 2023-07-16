package com.kotlinspirit.rangeres.core

import com.kotlinspirit.core.*
import com.kotlinspirit.rangeres.ParseRange

internal class RangeResultRuleGetRangeCore<T: Any>(
    rule: Rule<T>,
    val outRange: ParseRange
) : RangeResultRuleCore<T>(rule) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return rule.parse(seek, string).also {
            if (it.isComplete) {
                outRange.startSeek = seek
                outRange.endSeek = it.seek
            } else {
                outRange.startSeek = -1
                outRange.endSeek = -1
            }
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (!result.isError) {
            outRange.startSeek = seek
            outRange.endSeek = result.endSeek
        } else {
            outRange.startSeek = -1
            outRange.endSeek = -1
        }
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return rule.reverseParse(seek, string).also {
            if (it.isComplete) {
                outRange.startSeek = it.seek + 1
                outRange.endSeek = seek + 1
            } else {
                outRange.startSeek = -1
                outRange.endSeek = -1
            }
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (!result.isError) {
            outRange.startSeek = result.endSeek + 1
            outRange.endSeek = seek + 1
        } else {
            outRange.startSeek = -1
            outRange.endSeek = -1
        }
    }

    override val debugName: String
        get() = "getRange"
}