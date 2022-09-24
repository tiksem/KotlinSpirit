package com.kotlinspirit.expressive

import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

open class OptionalRule<T : Any>(
    protected val rule: Rule<T>
) : RuleWithDefaultRepeat<T>() {

    override fun parse(seek: Int, string: CharSequence): Long {
        val res = rule.parse(seek, string)
        if (res.getParseCode().isNotError()) {
            return res
        }

        return createComplete(seek)
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<T>
    ) {
        rule.parseWithResult(seek, string, result)
        if (result.isError) {
            result.data = null
        }
        result.parseResult = createStepResult(
            seek = result.seek,
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return -seek - 1
    }

    override fun clone(): OptionalRule<T> {
        return OptionalRule(rule)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): OptionalRule<T> {
        return DebugOptionalRule(name ?: "optional(${rule.debugName})", rule.internalDebug())
    }

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun ignoreCallbacks(): OptionalRule<T> {
        return OptionalRule(rule.ignoreCallbacks())
    }
}

private class DebugOptionalRule<T : Any>(
    override val name: String,
    rule: Rule<T>
): OptionalRule<T>(rule), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<T>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}

open class OptionalCharRule(rule: CharRule) : OptionalRule<Char>(rule) {
    override fun clone(): OptionalCharRule {
        return OptionalCharRule((rule as CharRule).clone())
    }

    override fun debug(name: String?): OptionalCharRule {
        val debug = rule.internalDebug()
        return DebugOptionalCharRule(name ?: "optional(${debug.debugName})",
            debug as CharRule
        )
    }
}

private class DebugOptionalCharRule(
    override val name: String,
    rule: CharRule
): OptionalCharRule(rule), DebugRule {
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

    override fun clone(): OptionalCharRule {
        return DebugOptionalCharRule(name, rule.clone() as CharRule)
    }
}

