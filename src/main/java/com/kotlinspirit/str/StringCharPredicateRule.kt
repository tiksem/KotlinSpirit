package com.kotlinspirit.str

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

open class StringCharPredicateRule(
    private val predicate: (Char) -> Boolean,
    name: String? = null
) : RuleWithDefaultRepeat<CharSequence>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        var i = seek
        while (i < string.length) {
            val c = string[i]
            if (!predicate(c)) {
                return createComplete(i)
            }

            i++
        }

        return createComplete(i)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        var i = seek
        while (i < string.length) {
            val c = string[i]
            if (!predicate(c)) {
                result.data = string.subSequence(seek, i)
                result.parseResult = createComplete(i)
                return
            }

            i++
        }

        result.data = string.subSequence(seek, i)
        result.parseResult = createComplete(i)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun clone(): StringCharPredicateRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): StringCharPredicateRule {
        return StringCharPredicateRule(predicate, name)
    }

    override val defaultDebugName: String
        get() = "stringIf"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun ignoreCallbacks(): StringCharPredicateRule {
        return this
    }

    override fun getPrefixMaxLength(): Int {
        return 0
    }

    override fun isPrefixFixedLength(): Boolean {
        return true
    }
}