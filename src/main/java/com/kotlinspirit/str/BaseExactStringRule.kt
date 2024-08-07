package com.kotlinspirit.str

import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

abstract class BaseExactStringRule<T : Any>(
    internal val string: CharSequence,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return exactStringParse(seek, string, this.string, false)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return string.regionMatches(
            thisOffset = seek,
            other = this.string,
            otherOffset = 0,
            length = this.string.length
        )
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return exactStringReverseParse(seek, string, this.string, false)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return string.regionMatches(
            thisOffset = seek - this.string.length + 1,
            other = this.string,
            otherOffset = 0,
            length = this.string.length
        )
    }

    override fun isThreadSafe(): Boolean {
        return true
    }
}