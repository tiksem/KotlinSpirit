package com.kotlinspirit.rangeres.result

import com.kotlinspirit.core.Rule
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.rangeres.base.BaseRangeResultCharRule
import com.kotlinspirit.rangeres.core.RangeResultRuleResultCallbacksCore

internal open class RangeResultCharCallbacksResultRule(
    rule: Rule<Char>,
    private val callback: (ParseRangeResult<Char>) -> Unit,
    name: String? = null
) : BaseRangeResultCharRule(
    core = RangeResultRuleResultCallbacksCore(rule, callback),
    name = name
) {
    override fun clone(): RangeResultCharCallbacksResultRule {
        return RangeResultCharCallbacksResultRule(
            rule = core.rule.clone(),
            callback = callback,
            name = name
        )
    }

    override fun name(name: String): RangeResultCharCallbacksResultRule {
        return RangeResultCharCallbacksResultRule(rule = core.rule, callback = callback, name = name)
    }
}