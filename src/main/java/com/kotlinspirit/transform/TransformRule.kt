package com.kotlinspirit.transform

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class TransformRule<From : Any, To : Any>(
    private val from: Rule<From>,
    private val transformer: (From) -> To,
    name: String? = null
) : RuleWithDefaultRepeat<To>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return from.parse(seek, string)
    }

    private inline fun baseParseWithResult(
        seek: Int,
        string: CharSequence,
        doParse: Rule<From>.(Int, CharSequence, ParseResult<From>) -> Unit,
        result: ParseResult<To>
    ) {
        val tempResult = ParseResult<From>()
        from.doParse(seek, string, tempResult)
        result.parseResult = tempResult.parseResult
        val data = tempResult.data
        if (data == null) {
            result.data = null
        } else {
            result.data = transformer(data)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<To>) {
        baseParseWithResult(
            seek = seek,
            string = string,
            result = result,
            doParse = Rule<From>::parseWithResult
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return from.hasMatch(seek, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return from.reverseParse(seek, string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<To>) {
        baseParseWithResult(
            seek = seek,
            string = string,
            result = result,
            doParse = Rule<From>::reverseParseWithResult
        )
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return from.reverseHasMatch(seek, string)
    }

    override fun isThreadSafe(): Boolean {
        return false
    }

    override fun name(name: String): TransformRule<From, To> {
        return TransformRule(from, transformer, name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false
    override val defaultDebugName: String
        get() = "(${from.defaultDebugName}).transformed"

    override fun clone(): TransformRule<From, To> {
        return TransformRule(from.clone(), transformer, name)
    }
}