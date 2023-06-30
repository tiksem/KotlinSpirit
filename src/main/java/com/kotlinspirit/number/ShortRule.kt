package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class ShortRule(name: String? = null, private val radix: Int) : RuleWithDefaultRepeat<Short>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return IntParsers.parseInt(
            seek = seek,
            string = string,
            radix = radix,
            invalidIntParseCode = ParseCode.INVALID_SHORT,
            outOfBoundsParseCode = ParseCode.SHORT_OUT_OF_BOUNDS,
            onResult = {
                if (it !in Short.MIN_VALUE..Short.MAX_VALUE) {
                    return createStepResult(
                        seek = seek,
                        parseCode = ParseCode.SHORT_OUT_OF_BOUNDS
                    )
                }
            }
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<Short>) {
        r.data = null
        r.parseResult = IntParsers.parseInt(
            seek = seek,
            string = string,
            radix = radix,
            invalidIntParseCode = ParseCode.INVALID_SHORT,
            outOfBoundsParseCode = ParseCode.SHORT_OUT_OF_BOUNDS,
            onResult = {
                if (it !in Short.MIN_VALUE..Short.MAX_VALUE) {
                    r.parseResult = createStepResult(
                        seek = seek,
                        parseCode = ParseCode.SHORT_OUT_OF_BOUNDS
                    )
                    return
                } else {
                    r.data = it.toShort()
                }
            }
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return IntParsers.reverseParse(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_SHORT,
            outOfBoundsParseCode = ParseCode.SHORT_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it > Short.MAX_VALUE
            }
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, r: ParseResult<Short>) {
        IntParsers.reverseParseWithResult(
            seek = seek,
            string = string,
            invalidIntParseCode = ParseCode.INVALID_SHORT,
            outOfBoundsParseCode = ParseCode.SHORT_OUT_OF_BOUNDS,
            checkOutOfBounds = {
                it > Short.MAX_VALUE
            }
        ) { value, parseResult ->
            r.parseResult = parseResult
            r.data = value?.toShort()
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode() == ParseCode.COMPLETE
    }

    override fun clone(): ShortRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): ShortRule {
        return ShortRule(name, radix)
    }

    override val defaultDebugName: String
        get() = "short"

    override fun isThreadSafe(): Boolean {
        return true
    }
}