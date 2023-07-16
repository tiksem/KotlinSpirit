package com.kotlinspirit.number

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class ByteRule(name: String? = null, private val radix: Int) : RuleWithDefaultRepeat<Byte>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return IntParsers.parseInt(
            seek = seek,
            string = string,
            radix = radix,
            invalidIntParseCode = ParseCode.INVALID_BYTE,
            outOfBoundsParseCode = ParseCode.BYTE_OUT_OF_BOUNDS,
            onResult = {
                if (it !in Byte.MIN_VALUE..Byte.MAX_VALUE) {
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = ParseCode.BYTE_OUT_OF_BOUNDS
                    )
                }
            }
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<Byte>) {
        r.data = null
        r.parseResult = IntParsers.parseInt(
            seek = seek,
            string = string,
            radix = radix,
            invalidIntParseCode = ParseCode.INVALID_BYTE,
            outOfBoundsParseCode = ParseCode.BYTE_OUT_OF_BOUNDS,
            onResult = {
                if (it !in Byte.MIN_VALUE..Byte.MAX_VALUE) {
                    r.parseResult = ParseSeekResult(
                        seek = seek,
                        parseCode = ParseCode.BYTE_OUT_OF_BOUNDS
                    )
                    return
                } else {
                    r.data = it.toByte()
                }
            }
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).parseCode == ParseCode.COMPLETE
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return IntParsers.reverseParse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_BYTE,
            outOfBoundsParseCode = ParseCode.BYTE_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it > Byte.MAX_VALUE
            }
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, r: ParseResult<Byte>) {
        IntParsers.reverseParseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_BYTE,
            outOfBoundsParseCode = ParseCode.BYTE_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it > Byte.MAX_VALUE
            }
        ) { value, parseResult ->
            r.parseResult = parseResult
            r.data = value?.toByte()
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).parseCode == ParseCode.COMPLETE
    }

    override fun clone(): ByteRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): ByteRule {
        return ByteRule(name, radix)
    }

    override val defaultDebugName: String
        get() = "byte"

    override fun isThreadSafe(): Boolean {
        return true
    }
}