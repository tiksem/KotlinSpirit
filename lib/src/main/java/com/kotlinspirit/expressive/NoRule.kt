package com.kotlinspirit.expressive

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

open class NoRule(
    protected val rule: Rule<*>
) : RuleWithDefaultRepeat<CharSequence>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        return rule.noParse(seek, string).let {
            if (it < 0) {
                return createStepResult(
                    seek = -it - 1,
                    parseCode = ParseCode.NO_FAILED
                )
            } else {
                createComplete(it)
            }
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        val parseResult = rule.noParse(seek, string)
        if (parseResult >= 0) {
            result.data = string.subSequence(seek, parseResult)
            result.parseResult = createStepResult(
                seek = parseResult,
                parseCode = ParseCode.COMPLETE
            )
        } else {
            result.parseResult = createStepResult(
                seek = -parseResult - 1,
                parseCode = ParseCode.NO_FAILED
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return !rule.hasMatch(seek, string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return rule.parse(seek, string).let {
            if (it.getParseCode().isNotError()) {
                it.getSeek()
            } else {
                -it.getSeek() - 1
            }
        }
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
        seek: Int, string: CharSequence, result: ParseResult<CharSequence>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }

    override fun clone(): NoRule {
        return DebugNoRule(name, rule.clone())
    }
}