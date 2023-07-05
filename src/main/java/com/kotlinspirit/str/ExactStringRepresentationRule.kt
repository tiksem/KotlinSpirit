package com.kotlinspirit.str

import com.kotlinspirit.core.ParseResult

abstract class ExactStringRepresentationRule<T : Any>(
    protected val obj: T,
    name: String? = null
) : BaseExactStringRule<T>(obj.toString(), name) {
    private fun ParseResult<T>.assignObject() {
        data = if (isError) {
            null
        } else {
            obj
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        result.parseResult = parse(seek, string)
        result.assignObject()
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        result.parseResult = reverseParse(seek, string)
        result.assignObject()
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false
}