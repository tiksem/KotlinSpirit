package com.kotlinspirit.number

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class ULongRule(name: String? = null) : RuleWithDefaultRepeat<ULong>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return UIntParsers.parse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_ULONG,
            outOfBoundsParseCode = ParseCode.ULONG_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after < before
            }
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<ULong>) {
        UIntParsers.parseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_ULONG,
            outOfBoundsParseCode = ParseCode.ULONG_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after < before
            }
        ) { value, parseResult ->
            r.data = value
            r.parseResult = parseResult
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).parseCode == ParseCode.COMPLETE
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return UIntParsers.reverseParse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_ULONG,
            outOfBoundsParseCode = ParseCode.ULONG_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after < before
            }
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<ULong>) {
        UIntParsers.reverseParseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_ULONG,
            outOfBoundsParseCode = ParseCode.ULONG_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after < before
            }
        ) { value, parseResult ->
            result.data = value
            result.parseResult = parseResult
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return reverseParse(seek, string).parseCode == ParseCode.COMPLETE
    }

    override fun clone(): ULongRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): ULongRule {
        return ULongRule(name)
    }

    override val defaultDebugName: String
        get() = "ulong"

    override fun isThreadSafe(): Boolean {
        return true
    }
}