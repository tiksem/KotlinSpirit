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
    internal val outRange: ParseRange,
    name: String? = null
) : BaseRangeResultCharRule(
    core = RangeResultRuleGetRangeCore(rule, outRange),
    name
) {
    override fun clone(): CharRule {
        return RangeResultCharRule(rule = core.rule.clone(), outRange, name)
    }

    override fun debug(engine: DebugEngine): DebugRule<Char> {
        return DebugRule(
            rule = core.rule.debug(engine),
            engine = engine
        )
    }

    override fun name(name: String): RangeResultCharRule {
        return RangeResultCharRule(rule = core.rule, outRange, name)
    }
}