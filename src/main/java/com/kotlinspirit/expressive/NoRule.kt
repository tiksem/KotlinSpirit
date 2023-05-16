package com.kotlinspirit.expressive

import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule

/**
 * Matches one character, if it doesn't match the original rule.
 * If we are at the end of input, and the original rule doesn't match EOF, it outputs '\0' as a result
 */
class NoRule(
    private val rule: Rule<*>,
    name: String? = null
) : CharRule(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        val rResult = rule.parse(seek, string)
        val parseCode = rResult.getParseCode()
        return when (parseCode) {
            ParseCode.COMPLETE -> createStepResult(
                seek = seek,
                parseCode = ParseCode.NO_FAILED
            )
            ParseCode.EOF -> createComplete(string.length)
            else -> createComplete(seek + 1)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        val rResult = rule.parse(seek, string)
        val parseCode = rResult.getParseCode()
        when (parseCode) {
            ParseCode.COMPLETE -> {
                result.parseResult = createStepResult(
                    seek = seek,
                    parseCode = ParseCode.NO_FAILED
                )
            }
            ParseCode.EOF -> {
                result.parseResult = createComplete(seek)
                result.data = 0.toChar()
            }
            else -> {
                result.parseResult = createComplete(seek + 1)
                result.data = string[seek]
            }
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.parse(seek, string).getParseCode().isError()
    }

    override fun clone(): NoRule {
        return NoRule(rule.clone(), name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "!${rule.wrappedName}"

    override fun debug(engine: DebugEngine): DebugRule<Char> {
        return DebugRule(
            rule = NoRule(rule.debug(engine), name),
            engine = engine
        )
    }

    override fun name(name: String): NoRule {
        return NoRule(rule, name)
    }

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun getPrefixMaxLength(): Int {
        return rule.getPrefixMaxLength()
    }

    override fun isPrefixFixedLength(): Boolean {
        return rule.isPrefixFixedLength()
    }

    override fun ignoreCallbacks(): NoRule {
        return NoRule(rule.ignoreCallbacks())
    }
}