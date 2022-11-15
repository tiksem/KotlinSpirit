package com.kotlinspirit.rangeres.callbacks

import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.base.BaseRangeResultCharRule
import com.kotlinspirit.rangeres.core.RangeResultRuleCallbacksCore

internal open class RangeResultCharCallbacksRule(
    rule: Rule<Char>,
    private val callback: (ParseRange) -> Unit
) : BaseRangeResultCharRule(
    core = RangeResultRuleCallbacksCore<Char>(rule, callback)
){
    override fun clone(): CharRule {
        return RangeResultCharCallbacksRule(
            rule = core.rule.clone(),
            callback = callback
        )
    }

    override fun debug(name: String?): CharRule {
        return DebugRangeResultCharCallbacksRule(
            name = name ?: "rangeResult",
            rule = core.rule.internalDebug(),
            callback = callback
        )
    }
}

private class DebugRangeResultCharCallbacksRule(
    override val name: String,
    rule: Rule<Char>,
    callback: (ParseRange) -> Unit
) : RangeResultCharCallbacksRule(rule, callback), DebugRule {
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