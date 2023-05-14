package com.kotlinspirit.dynamic

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.str.exactStringParse
import com.kotlinspirit.str.exactStringParseWithResult

class DynamicStringRule(
    internal val stringProvider: () -> CharSequence,
    name: String? = null,
) : RuleWithDefaultRepeat<CharSequence>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return exactStringParse(
            seek = seek,
            string = string,
            token = stringProvider()
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        exactStringParseWithResult(
            seek = seek,
            string = string,
            token = stringProvider(),
            result = result
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

    override fun ignoreCallbacks(): DynamicStringRule {
        return this
    }

    override fun isThreadSafe(): Boolean {
        return false
    }

    override fun isDynamic(): Boolean {
        return true
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