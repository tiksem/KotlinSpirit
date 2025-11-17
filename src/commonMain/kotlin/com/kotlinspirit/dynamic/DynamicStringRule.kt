package com.kotlinspirit.dynamic

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.str.exactStringParse
import com.kotlinspirit.str.exactStringParseWithResult
import com.kotlinspirit.str.exactStringReverseParse
import com.kotlinspirit.str.exactStringReverseParseWithResult

class DynamicStringRule(
    internal val stringProvider: () -> CharSequence,
    name: String? = null,
) : RuleWithDefaultRepeat<CharSequence>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return exactStringParse(
            seek = seek,
            string = string,
            token = stringProvider(),
            ignoreCase = false
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        exactStringParseWithResult(
            seek = seek,
            string = string,
            token = stringProvider(),
            result = result,
            ignoreCase = false
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val other = stringProvider()
        return string.regionMatches(
            thisOffset = seek,
            other = other,
            otherOffset = 0,
            length = other.length
        )
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return exactStringReverseParse(
            seek = seek,
            string = string,
            token = stringProvider(),
            ignoreCase = false
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        exactStringReverseParseWithResult(
            seek = seek,
            string = string,
            token = stringProvider(),
            result = result
        )
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        val other = stringProvider()
        return string.regionMatches(
            thisOffset = seek - other.length + 1,
            other = other,
            otherOffset = 0,
            length = other.length
        )
    }

    override fun isThreadSafe(): Boolean {
        return false
    }

    override fun name(name: String): DynamicStringRule {
        return DynamicStringRule(stringProvider, name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false
    override val defaultDebugName: String
        get() = "dynamicString(${stringProvider()})"

    override fun clone(): DynamicStringRule {
        return this
    }
}