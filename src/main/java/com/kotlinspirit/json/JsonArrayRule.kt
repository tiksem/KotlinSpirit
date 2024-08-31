package com.kotlinspirit.json

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class JsonArrayRule(name: String? = null) : RuleWithDefaultRepeat<ParseRange>(name) {
    override fun clone(): JsonArrayRule {
        return this
    }

    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        val parser = JsonParser()
        val endSeek = parser.parseJsonArray(seek, string)
            ?: return ParseSeekResult(seek, ParseCode.INVALID_JSON_ARRAY)

        return ParseSeekResult(endSeek)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).isComplete
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        val parser = ReverseJsonParser()
        val endSeek = parser.parseJsonArray(seek, string)
            ?: return ParseSeekResult(seek, ParseCode.INVALID_JSON_ARRAY)

        return ParseSeekResult(endSeek)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return reverseParse(seek, string).isComplete
    }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun name(name: String): JsonArrayRule {
        return JsonArrayRule(name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false
    override val defaultDebugName: String
        get() = "jsonArray"

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<ParseRange>) {
        result.parseResult = reverseParse(seek, string)
        if (result.parseResult.isComplete) {
            result.data = ParseRange(result.parseResult.seek + 1, seek + 1)
        } else {
            result.data = null
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<ParseRange>) {
        result.parseResult = parse(seek, string)
        if (result.parseResult.isComplete) {
            result.data = ParseRange(seek, result.parseResult.seek)
        } else {
            result.data = null
        }
    }

}