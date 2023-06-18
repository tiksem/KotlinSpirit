package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class UShortRule(name: String? = null) : RuleWithDefaultRepeat<UShort>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return UIntParsers.parse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_USHORT,
            outOfBoundsParseCode = ParseCode.USHORT_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after > UShort.MAX_VALUE
            }
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<UShort>) {
        UIntParsers.parseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_USHORT,
            outOfBoundsParseCode = ParseCode.USHORT_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after > UShort.MAX_VALUE
            }
        ) { value, parseResult ->
            r.data = value?.toUShort()
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
            invalidIntParseCode = ParseCode.INVALID_USHORT,
            outOfBoundsParseCode = ParseCode.USHORT_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after > UShort.MAX_VALUE
            }
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<UShort>) {
        UIntParsers.reverseParseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_USHORT,
            outOfBoundsParseCode = ParseCode.USHORT_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after > UShort.MAX_VALUE
            }
        ) { value, parseResult ->
            result.data = value?.toUShort()
            result.parseResult = parseResult
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return reverseParse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun clone(): UShortRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): UShortRule {
        return UShortRule(name)
    }

    override val defaultDebugName: String
        get() = "ushort"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun ignoreCallbacks(): UShortRule {
        return this
    }
}