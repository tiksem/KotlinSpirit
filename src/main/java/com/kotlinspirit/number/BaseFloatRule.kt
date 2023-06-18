package com.kotlinspirit.number

import com.kotlinspirit.repeat.RuleWithDefaultRepeat

abstract class BaseFloatRule<T : Any>(
    name: String?,
    internal val invalidFloatErrorCode: Int
) : RuleWithDefaultRepeat<T>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return FloatParsers.parse(
            seek = seek,
            string = string,
            invalidFloatErrorCode = invalidFloatErrorCode
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return FloatParsers.hasMatch(seek, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return FloatParsers.reverseParse(
            seek = seek,
            string = string,
            invalidFloatErrorCode = invalidFloatErrorCode
        )
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