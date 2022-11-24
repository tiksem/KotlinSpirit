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
    private val callback: (ParseRange) -> Unit,
    name: String? = null
) : BaseRangeResultCharRule(
    core = RangeResultRuleCallbacksCore<Char>(rule, callback),
    name
){
    override fun clone(): CharRule {
        return RangeResultCharCallbacksRule(
            rule = core.rule.clone(),
            callback = callback,
            name = name
        )
    }

    override fun name(name: String): RangeResultCharCallbacksRule {
        return RangeResultCharCallbacksRule(rule = core.rule, callback = callback, name = name)
    }

    override fun debug(engine: DebugEngine): DebugRule<Char> {
        return DebugRule(
            rule = RangeResultCharCallbacksRule(rule = core.rule.debug(engine), callback = callback, name = name),
            engine = engine
        )
    }
}