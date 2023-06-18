package com.kotlinspirit.expressive

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isError
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class RequiresPrefixRule<T : Any>(
    private val prefixRule: Rule<*>,
    private val bodyRule: Rule<T>,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        val bodyParseResult = bodyRule.parse(seek, string)
        if (bodyParseResult.getParseCode().isError()) {
            return bodyParseResult
        }

        val reverseParseResult = prefixRule.reverseParse(seek - 1, string)
        if (reverseParseResult.getParseCode().isNotError()) {
            return bodyParseResult
        }

        return createStepResult(
            seek = seek,
            parseCode = ParseCode.PREFIX_NOT_SATISFIED + reverseParseResult.getParseCode()
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        bodyRule.parseWithResult(seek, string, result)
        if (result.isError) {
            return
        }

        val prefixParseResult = prefixRule.reverseParse(seek - 1, string)
        if (prefixParseResult.getParseCode().isNotError()) {
            return
        }

        result.parseResult = createStepResult(
            seek = seek,
            parseCode = ParseCode.PREFIX_NOT_SATISFIED + prefixParseResult.getParseCode()
        )
        result.data = null
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return bodyRule.hasMatch(seek, string) && prefixRule.reverseHasMatch(seek - 1, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        val bodyParseResult = bodyRule.reverseParse(seek, string)
        if (bodyParseResult.getParseCode().isError()) {
            return bodyParseResult
        }

        val prefixParseResult = prefixRule.reverseParse(bodyParseResult.getSeek(), string)
        if (prefixParseResult.getParseCode().isNotError()) {
            return bodyParseResult
        }

        return createStepResult(
            seek = seek,
            parseCode = ParseCode.PREFIX_NOT_SATISFIED + prefixParseResult.getParseCode()
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        bodyRule.reverseParseWithResult(seek, string, result)
        if (result.isError) {
            return
        }

        val prefixParseResult = prefixRule.reverseParse(result.endSeek, string)
        if (prefixParseResult.getParseCode().isNotError()) {
            return
        }

        result.parseResult = createStepResult(
            seek = seek,
            parseCode = ParseCode.PREFIX_NOT_SATISFIED + prefixParseResult.getParseCode()
        )
        result.data = null
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return bodyRule.reverseParse(seek, string).let {
            if (it.getParseCode().isError()) {
                false
            } else {
                prefixRule.reverseHasMatch(seek = it.getSeek(), string = string)
            }
        }
    }

    override fun clone(): RequiresPrefixRule<T> {
        return RequiresPrefixRule(prefixRule.clone(), bodyRule.clone(), name)
    }

    override fun ignoreCallbacks(): RequiresPrefixRule<T> {
        return RequiresPrefixRule(prefixRule.ignoreCallbacks(), bodyRule.ignoreCallbacks(), name)
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