package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class UIntRule(name: String? = null) : RuleWithDefaultRepeat<UInt>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return UIntParsers.parse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_UINT,
            outOfBoundsParseCode = ParseCode.UINT_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after > UInt.MAX_VALUE
            }
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<UInt>) {
        UIntParsers.parseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_UINT,
            outOfBoundsParseCode = ParseCode.UINT_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after > UInt.MAX_VALUE
            }
        ) { value, parseResult ->
            r.data = value?.toUInt()
            r.parseResult = parseResult
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return UIntParsers.reverseParse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_UINT,
            outOfBoundsParseCode = ParseCode.UINT_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after > UInt.MAX_VALUE
            }
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<UInt>) {
        UIntParsers.reverseParseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_UINT,
            outOfBoundsParseCode = ParseCode.UINT_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after > UInt.MAX_VALUE
            }
        ) { value, parseResult ->
            result.data = value?.toUInt()
            result.parseResult = parseResult
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return reverseParse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun clone(): UIntRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): UIntRule {
        return UIntRule(name)
    }

    override val defaultDebugName: String
        get() = "uint"

    override fun isThreadSafe(): Boolean {
        return true
    }
}