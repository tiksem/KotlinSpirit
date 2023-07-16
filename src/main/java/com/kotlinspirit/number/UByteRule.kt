package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class UByteRule(name: String? = null) : RuleWithDefaultRepeat<UByte>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return UIntParsers.parse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_UBYTE,
            outOfBoundsParseCode = ParseCode.UBYTE_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after > UByte.MAX_VALUE
            }
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<UByte>) {
        UIntParsers.parseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_UBYTE,
            outOfBoundsParseCode = ParseCode.UBYTE_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after > UByte.MAX_VALUE
            }
        ) { value, parseResult ->
            r.data = value?.toUByte()
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
            invalidIntParseCode = ParseCode.INVALID_UBYTE,
            outOfBoundsParseCode = ParseCode.UBYTE_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after > UByte.MAX_VALUE
            }
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<UByte>) {
        UIntParsers.reverseParseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_UBYTE,
            outOfBoundsParseCode = ParseCode.UBYTE_OUT_OF_BOUNDS,
            checkOutOfBounds = { before, after ->
                after > UByte.MAX_VALUE
            }
        ) { value, parseResult ->
            result.data = value?.toUByte()
            result.parseResult = parseResult
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return reverseParse(seek, string).parseCode == ParseCode.COMPLETE
    }

    override fun clone(): UByteRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): UByteRule {
        return UByteRule(name)
    }

    override val defaultDebugName: String
        get() = "ubyte"

    override fun isThreadSafe(): Boolean {
        return true
    }
}