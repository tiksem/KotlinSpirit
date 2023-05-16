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
    override fun clone(): RequiresPrefixRule<T> {
        return RequiresPrefixRule(prefixRule.clone(), bodyRule.clone(), name)
    }

    private fun checkPrefix(seek: Int, string: CharSequence): Boolean {
        if (prefixRule.isPrefixFixedLength()) {
            val length = prefixRule.getPrefixMaxLength()
            val prefixSearchBeginSeek = seek - length
            return prefixSearchBeginSeek >= 0 && prefixRule.hasMatch(prefixSearchBeginSeek, string)
        }

        var prefixSearchBeginSeek = seek - prefixRule.getPrefixMaxLength()
        if (prefixSearchBeginSeek < 0) {
            prefixSearchBeginSeek = 0
        }

        var i = seek
        do {
            val prefixParseResult = prefixRule.parse(i, string)
            if (prefixParseResult.getParseCode().isNotError()) {
                if (prefixParseResult.getSeek() == seek) {
                    return true
                }
            }
            i--
        } while (i >= prefixSearchBeginSeek)

        return false
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        val bodyResult = bodyRule.parse(seek, string)
        if (bodyResult.getParseCode().isError()) {
            return bodyResult
        }

        return if (checkPrefix(seek, string)) {
            bodyResult
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.PREFIX_NOT_SATISFIED
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        if (!bodyRule.hasMatch(seek, string)) {
            return false
        }

        return checkPrefix(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        bodyRule.parseWithResult(seek, string, result)
        if (result.isError) {
            return
        }

        if (!checkPrefix(seek, string)) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.PREFIX_NOT_SATISFIED
            )
            result.data = null
        }
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
        get() = "${bodyRule.wrappedName}.withPrefix(${prefixRule.wrappedName})"
}