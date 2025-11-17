package com.kotlinspirit.expressive

import com.kotlinspirit.core.*
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class RequiresPrefixRule<T : Any>(
    private val prefixRule: Rule<*>,
    private val bodyRule: Rule<T>,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        val bodyParseResult = bodyRule.parse(seek, string)
        if (bodyParseResult.isError) {
            return bodyParseResult
        }

        val reverseParseResult = prefixRule.reverseParse(seek - 1, string)
        if (reverseParseResult.isComplete) {
            return bodyParseResult
        }

        return ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.PREFIX_NOT_SATISFIED + reverseParseResult.parseCode
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        bodyRule.parseWithResult(seek, string, result)
        if (result.isError) {
            return
        }

        val prefixParseResult = prefixRule.reverseParse(seek - 1, string)
        if (prefixParseResult.isComplete) {
            return
        }

        result.parseResult = ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.PREFIX_NOT_SATISFIED + prefixParseResult.parseCode
        )
        result.data = null
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return bodyRule.hasMatch(seek, string) && prefixRule.reverseHasMatch(seek - 1, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        val bodyParseResult = bodyRule.reverseParse(seek, string)
        if (bodyParseResult.isError) {
            return bodyParseResult
        }

        val prefixParseResult = prefixRule.reverseParse(bodyParseResult.seek, string)
        if (prefixParseResult.isComplete) {
            return bodyParseResult
        }

        return ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.PREFIX_NOT_SATISFIED + prefixParseResult.parseCode
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        bodyRule.reverseParseWithResult(seek, string, result)
        if (result.isError) {
            return
        }

        val prefixParseResult = prefixRule.reverseParse(result.endSeek, string)
        if (prefixParseResult.isComplete) {
            return
        }

        result.parseResult = ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.PREFIX_NOT_SATISFIED + prefixParseResult.parseCode
        )
        result.data = null
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return bodyRule.reverseParse(seek, string).let {
            if (it.isError) {
                false
            } else {
                prefixRule.reverseHasMatch(seek = it.seek, string = string)
            }
        }
    }

    override fun clone(): RequiresPrefixRule<T> {
        return RequiresPrefixRule(prefixRule.clone(), bodyRule.clone(), name)
    }

    override fun isThreadSafe(): Boolean {
        return prefixRule.isThreadSafe() && bodyRule.isThreadSafe()
    }

    override fun name(name: String): RequiresPrefixRule<T> {
        return RequiresPrefixRule(prefixRule, bodyRule, name)
    }

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            RequiresPrefixRule(
                prefixRule = prefixRule.debug(engine),
                bodyRule = bodyRule.debug(engine),
                name = name
            ),
            engine = engine
        )
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false
    override val defaultDebugName: String
        get() = "${bodyRule.wrappedName}.requiresPrefix(${prefixRule.wrappedName})"
}