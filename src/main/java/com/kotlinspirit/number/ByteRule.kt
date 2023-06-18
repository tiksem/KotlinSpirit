package com.kotlinspirit.number

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class ByteRule(name: String? = null) : RuleWithDefaultRepeat<Byte>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return IntParsers.parse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_BYTE,
            outOfBoundsParseCode = ParseCode.BYTE_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it > Byte.MAX_VALUE
            }
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<Byte>) {
        IntParsers.parseWithResult(
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

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
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
        return parse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun clone(): ByteRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): ByteRule {
        return ByteRule(name)
    }

    override val defaultDebugName: String
        get() = "byte"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun ignoreCallbacks(): ByteRule {
        return this
    }
}