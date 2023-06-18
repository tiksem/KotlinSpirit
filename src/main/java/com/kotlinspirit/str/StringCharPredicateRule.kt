package com.kotlinspirit.str

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.getSeek
import com.kotlinspirit.ext.moveSeekReverseUntilDontMatch
import com.kotlinspirit.ext.moveSeekUntilDontMatch
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import kotlin.math.max
import kotlin.math.min

open class StringCharPredicateRule(
    private val predicate: (Char) -> Boolean,
    name: String? = null
) : RuleWithDefaultRepeat<CharSequence>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return string.moveSeekUntilDontMatch(
            startIndex = seek,
            endIndex = string.length,
            predicate = predicate
        ).let {
            createComplete(it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        val parseResult = parse(seek, string)
        result.data = string.subSequence(seek, parseResult.getSeek())
        result.parseResult = parseResult
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return string.moveSeekReverseUntilDontMatch(
            startIndex = seek,
            endIndex = -1,
            predicate = predicate
        ).let {
            createComplete(it)
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        val parseResult = reverseParse(seek, string)
        result.data = string.subSequence(parseResult.getSeek() + 1, seek + 1)
        result.parseResult = parseResult
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
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
}