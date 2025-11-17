package com.kotlinspirit.number

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

abstract class BaseFloatRule<T : Any>(
    name: String?,
    internal val invalidFloatErrorCode: Int
) : RuleWithDefaultRepeat<T>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return FloatParsers.parse(
            seek = seek,
            string = string,
            invalidFloatErrorCode = invalidFloatErrorCode
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return FloatParsers.hasMatch(seek, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return FloatParsers.reverseParse(
            seek = seek,
            string = string,
            invalidFloatErrorCode = invalidFloatErrorCode
        )
    }

    // TODO: Optimize
    protected abstract fun String.getValue(): T

    protected fun String.isNan(): Boolean {
        return this[0] == 'N'
    }

    protected fun String.isPositiveInfinity(): Boolean {
        if (this.length < 2) {
            return false
        }

        return when (this[0]) {
            'i', 'I' -> true
            '+' -> {
                val c = this[1]
                c == 'i' || c == 'I'
            }
            else -> false
        }
    }

    protected fun String.isNegativeInfinity(): Boolean {
        if (this.length < 2) {
            return false
        }

        return when (this[0]) {
            '-' -> {
                val c = this[1]
                c == 'i' || c == 'I'
            }
            else -> false
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val parseResult = parse(seek, string)
        result.parseResult = parseResult
        if (parseResult.isComplete) {
            // TODO: Optimize
            result.data = string.substring(seek, parseResult.seek).getValue()
        } else {
            result.data = null
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val parseResult = reverseParse(seek, string)
        result.parseResult = parseResult
        if (parseResult.isComplete) {
            // TODO: Optimize
            result.data = string.substring(parseResult.seek + 1, seek + 1).getValue()
        } else {
            result.data = null
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return FloatParsers.reverseHasMatch(seek, string)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun isThreadSafe(): Boolean {
        return true
    }
}