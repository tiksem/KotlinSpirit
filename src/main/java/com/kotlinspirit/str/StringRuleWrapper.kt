package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.getSeek
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.repeat.RuleWithDefaultRepeatResult

open class StringRuleWrapper(
    protected val rule: Rule<*>
) : RuleWithDefaultRepeat<CharSequence>() {

    override fun invoke(callback: (CharSequence) -> Unit): RuleWithDefaultRepeatResult<CharSequence> {
        return RuleWithDefaultRepeatResult(this, callback)
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        return rule.parse(seek, string)
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<CharSequence>
    ) {
        val parseResult = rule.parse(seek, string)
        result.parseResult = parseResult
        if (parseResult.getParseCode().isNotError()) {
            result.data = string.subSequence(seek, parseResult.getSeek())
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
    }

    override fun clone(): StringRuleWrapper {
        return StringRuleWrapper(rule = rule.clone())
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): StringRuleWrapper {
        val debug = rule.internalDebug()
        return DebugStringRuleWrapper(
            name = name ?: "${debug.debugNameWrapIfNeed}.asString()",
            rule = debug
        )
    }

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun ignoreCallbacks(): StringRuleWrapper {
        return StringRuleWrapper(rule.ignoreCallbacks())
    }
}

private class DebugStringRuleWrapper(
    override val name: String,
    rule: Rule<*>
) : StringRuleWrapper(rule), DebugRule {
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

    override fun clone(): StringRuleWrapper {
        return DebugStringRuleWrapper(name, rule.clone())
    }
}
