package com.kotlinspirit.str

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.ext.all
import com.kotlinspirit.ext.debugString
import com.kotlinspirit.ext.moveSeekReverseUntilDontMatch
import com.kotlinspirit.ext.moveSeekUntilDontMatch
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import kotlin.math.max
import kotlin.math.min

open class StringCharPredicateRangeRule(
    private val predicate: (Char) -> Boolean,
    private val range: IntRange,
    name: String? = null
) : RuleWithDefaultRepeat<CharSequence>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        val first = range.first
        val lastSeek = seek + first
        if (lastSeek > string.length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
            )
        }

        if (!string.all(startIndex = seek, endIndex = lastSeek, predicate = predicate)) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
            )
        }

        val newSeek = string.moveSeekUntilDontMatch(
            startIndex = lastSeek,
            endIndex = min(seek + range.last.toLong(), string.length.toLong()).toInt(),
            predicate = predicate
        )

        return createComplete(newSeek)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        val parseResult = parse(seek, string)
        result.data = if (parseResult.getParseCode().isError()) {
            null
        } else {
            string.subSequence(seek, parseResult.getSeek())
        }
        result.parseResult = parseResult
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return string.all(
            startIndex = seek,
            endIndex = min(seek + range.first, string.length),
            predicate = predicate
        )
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        val lastSeek = seek - range.first + 1
        if (lastSeek < 0) {
            return createStepResult(
                seek = seek,
                ParseCode.STRING_NOT_ENOUGH_DATA
            )
        }

        if (!string.all(startIndex = lastSeek, endIndex = seek + 1, predicate = predicate)) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.STRING_NOT_ENOUGH_DATA
            )
        }

        val newSeek = string.moveSeekReverseUntilDontMatch(
            startIndex = seek,
            endIndex = max(-1, lastSeek - range.last),
            predicate = predicate
        )

        return createComplete(newSeek)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        val parseResult = reverseParse(seek, string)
        result.data = if (result.isError) {
            null
        } else {
            string.subSequence(parseResult.getSeek() + 1, seek + 1)
        }
        result.parseResult = parseResult
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return string.all(
            startIndex = max(0, seek - range.first + 1),
            endIndex = seek + 1,
            predicate = predicate
        )
    }

    override fun clone(): StringCharPredicateRangeRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(predicate, range, name)
    }

    override val defaultDebugName: String
        get() = "stringIf[${range.debugString}]"

    override fun isThreadSafe(): Boolean {
        return true
    }
}