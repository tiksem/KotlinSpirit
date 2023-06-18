package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.repeat.RuleWithDefaultRepeat 

class IntRule(name: String? = null) : RuleWithDefaultRepeat<Int>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return IntParsers.parse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_INT,
            outOfBoundsParseCode = ParseCode.INT_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it > Int.MAX_VALUE
            }
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<Int>) {
        IntParsers.parseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_INT,
            outOfBoundsParseCode = ParseCode.INT_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it > Int.MAX_VALUE
            }
        ) { value, parseResult ->
            r.parseResult = parseResult
            r.data = value?.toInt()
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return IntParsers.reverseParse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_INT,
            outOfBoundsParseCode = ParseCode.INT_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it > Int.MAX_VALUE
            }
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, r: ParseResult<Int>) {
        IntParsers.reverseParseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_INT,
            outOfBoundsParseCode = ParseCode.INT_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it > Int.MAX_VALUE
            }
        ) { value, parseResult ->
            r.parseResult = parseResult
            r.data = value?.toInt()
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun clone(): IntRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): IntRule {
        return IntRule(name)
    }

    override val defaultDebugName: String
        get() = "int"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun ignoreCallbacks(): IntRule {
        return this
    }
}