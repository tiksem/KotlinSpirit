package com.kotlinspirit.str

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.ext.debugString
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import kotlin.math.min

open class StringCharPredicateRangeRule(
    private val predicate: (Char) -> Boolean,
    private val range: IntRange,
    name: String? = null
) : RuleWithDefaultRepeat<CharSequence>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        var i = seek
        val limit = min(string.length, range.last + seek)
        while (i < limit) {
            val c = string[i]
            if (!predicate(c)) {
                return if (i - seek >= range.first) {
                    createComplete(i)
                } else {
                    createStepResult(
                        seek = i,
                        ParseCode.STRING_NOT_ENOUGH_DATA
                    )
                }
            }

            i++
        }

        return if (i - seek >= range.first) {
            createComplete(i)
        } else {
            createStepResult(
                seek = i,
                ParseCode.STRING_NOT_ENOUGH_DATA
            )
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        var i = seek
        val limit = min(string.length, range.last + seek)
        while (i < limit) {
            val c = string[i]
            if (!predicate(c)) {
                if (i - seek >= range.first) {
                    result.data = string.subSequence(seek, i)
                    result.parseResult = createComplete(i)
                } else {
                    result.parseResult = createStepResult(
                        seek = i,
                        ParseCode.STRING_NOT_ENOUGH_DATA
                    )
                }
                return
            }

            i++
        }

        if (i - seek >= range.first) {
            result.data = string.subSequence(seek, i)
            result.parseResult = createComplete(i)
        } else {
            result.parseResult = createStepResult(
                seek = i,
                ParseCode.STRING_NOT_ENOUGH_DATA
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < string.length && predicate(string[seek])
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

    override fun isDynamic(): Boolean {
        return false
    }

    override fun ignoreCallbacks(): StringCharPredicateRangeRule {
        return this
    }
}