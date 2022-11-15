package com.kotlinspirit.rangeres.result

import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.rangeres.base.BaseRangeResultCharRule
import com.kotlinspirit.rangeres.core.RangeResultGetRangeResultCore
import com.kotlinspirit.rangeres.core.RangeResultRuleResultCallbacksCore

internal open class RangeResultCharCallbacksResultRule(
    rule: Rule<Char>,
    private val callback: (ParseRangeResult<Char>) -> Unit
) : BaseRangeResultCharRule(
    core = RangeResultRuleResultCallbacksCore(rule, callback)
) {
    override fun clone(): RangeResultCharCallbacksResultRule {
        return RangeResultCharCallbacksResultRule(
            rule = core.rule.clone(),
            callback = callback
        )
    }

    override fun debug(name: String?): CharRule {
        return DebugRangeResultCharCallbacksResultRule(
            name = name ?: "rangeResult",
            rule = core.rule.internalDebug(),
            callback = callback
        )
    }
}

private class DebugRangeResultCharCallbacksResultRule(
    override val name: String,
    rule: Rule<Char>,
    callback: (ParseRangeResult<Char>) -> Unit
) : RangeResultCharCallbacksResultRule(rule, callback), DebugRule {
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
}