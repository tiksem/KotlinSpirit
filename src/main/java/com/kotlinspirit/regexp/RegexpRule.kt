package com.kotlinspirit.regexp

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class RegexpRule(private val regex: Regex, name: String? = null) : RuleWithDefaultRepeat<MatchResult>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        val match = regex.matchAt(string, seek)
        if (match != null) {
            return createComplete(match.range.last - 1)
        }

        return createStepResult(
            seek = seek,
            parseCode = ParseCode.REGEX_NO_MATCH
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<MatchResult>) {
        val match = regex.matchAt(string, seek)
        result.data = match
        result.parseResult = if (match != null) {
            createComplete(match.range.last - 1)
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.REGEX_NO_MATCH
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return regex.matchesAt(string, seek)
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        throw UnsupportedOperationException("regexp rule is not supported inside prefix rule yet")
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<MatchResult>) {
        throw UnsupportedOperationException("regexp rule is not supported inside prefix rule yet")
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        throw UnsupportedOperationException("regexp rule is not supported inside prefix rule yet")
    }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun name(name: String): RegexpRule {
        return RegexpRule(regex, name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false
    override val defaultDebugName: String
        get() = "regex(${regex.pattern})"

    override fun clone(): RegexpRule {
        return this
    }
}