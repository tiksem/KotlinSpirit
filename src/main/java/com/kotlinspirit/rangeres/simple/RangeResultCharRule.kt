package com.kotlinspirit.rangeres.simple

import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.base.BaseRangeResultCharRule
import com.kotlinspirit.rangeres.core.RangeResultRuleGetRangeCore

internal open class RangeResultCharRule(
    rule: Rule<Char>,
    internal val outRange: ParseRange
) : BaseRangeResultCharRule(
    core = RangeResultRuleGetRangeCore(rule, outRange)
) {
    override fun clone(): CharRule {
        return RangeResultCharRule(rule = core.rule.clone(), outRange)
    }

    override fun debug(name: String?): CharRule {
        return DebugRangeResultCharRule(
            name = name ?: "rangeResult",
            rule = core.rule.internalDebug(),
            outRange = outRange
        )
    }
}

private class DebugRangeResultCharRule(
    override val name: String,
    rule: Rule<Char>,
    outRange: ParseRange
) : RangeResultCharRule(rule, outRange), DebugRule {
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