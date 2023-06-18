package com.kotlinspirit.number

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class LongRule(name: String? = null) : RuleWithDefaultRepeat<Long>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return IntParsers.parse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_LONG,
            outOfBoundsParseCode = ParseCode.LONG_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it < 0
            }
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<Long>) {
        IntParsers.parseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_LONG,
            outOfBoundsParseCode = ParseCode.LONG_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it < 0
            }
        ) { value, parseResult ->
            r.parseResult = parseResult
            r.data = value
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return IntParsers.reverseParse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_LONG,
            outOfBoundsParseCode = ParseCode.LONG_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it < 0
            }
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, r: ParseResult<Long>) {
        IntParsers.reverseParseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_LONG,
            outOfBoundsParseCode = ParseCode.LONG_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it < 0
            }
        ) { value, parseResult ->
            r.parseResult = parseResult
            r.data = value
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun clone(): LongRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): LongRule {
        return LongRule(name)
    }

    override val defaultDebugName: String
        get() = "long"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun ignoreCallbacks(): LongRule {
        return this
    }
}