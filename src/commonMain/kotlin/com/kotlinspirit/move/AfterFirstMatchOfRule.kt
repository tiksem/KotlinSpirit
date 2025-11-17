package com.kotlinspirit.move

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.ext.findFirstRange
import com.kotlinspirit.ext.findFirstResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class AfterFirstMatchOfRule<T : Any>(
    private val rule: Rule<T>,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        val range = string.findFirstRange(rule)
            ?: return ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.AFTER_FIRST_RULE_MATCH_NOT_FOUND
            )

        return ParseSeekResult(seek = range.endSeek)
    }

    override fun clone(): AfterFirstMatchOfRule<T> {
        return AfterFirstMatchOfRule(rule.clone(), name)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val range = string.findFirstResult(rule)
        if (range == null) {
            result.parseResult = ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.AFTER_FIRST_RULE_MATCH_NOT_FOUND
            )
            result.data = null
            return
        }

        result.parseResult = ParseSeekResult(seek = range.endSeek)
        result.data = range.data
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        var i = 0
        while (i < string.length) {
            if (rule.hasMatch(i, string)) {
                return true
            }
            i++
        }

        return false
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        throw IllegalStateException("Reverse parse is not supported for afterFirstMatchOf")
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        throw IllegalStateException("Reverse parse is not supported for afterFirstMatchOf")
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        throw IllegalStateException("Reverse parse is not supported for afterFirstMatchOf")
    }

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun name(name: String): AfterFirstMatchOfRule<T> {
        return AfterFirstMatchOfRule(rule, name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false
    override val defaultDebugName: String
        get() = "afterFirstMatchOf(${rule.debugName})"
}