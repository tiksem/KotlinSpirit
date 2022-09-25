package com.kotlinspirit.expressive

import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

open class NoRule(
    protected val rule: Rule<*>
) : CharRule() {
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
        return NoRule(rule.clone())
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): NoRule {
        val debug = rule.internalDebug()
        return DebugNoRule(
            name = name ?: "!${debug.debugNameWrapIfNeed}",
            rule = debug
        )
    }

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun ignoreCallbacks(): NoRule {
        return NoRule(rule.ignoreCallbacks())
    }
}

private class DebugNoRule(
    override val name: String,
    rule: Rule<*>
) : NoRule(rule), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<Char>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }

    override fun clone(): NoRule {
        return DebugNoRule(name, rule.clone())
    }
}