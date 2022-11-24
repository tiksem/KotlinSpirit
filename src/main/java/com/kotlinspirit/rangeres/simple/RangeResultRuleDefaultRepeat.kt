package com.kotlinspirit.rangeres.simple

import com.kotlinspirit.core.Rule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.base.BaseRangeResultDefaultRepeatRule
import com.kotlinspirit.rangeres.core.RangeResultRuleGetRangeCore
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

internal open class RangeResultRuleDefaultRepeat<T : Any>(
    rule: Rule<T>,
    internal val outRange: ParseRange,
    name: String? = null
) : BaseRangeResultDefaultRepeatRule<T>(
    core = RangeResultRuleGetRangeCore<T>(rule, outRange),
    name
) {
    override fun clone(): RangeResultRuleDefaultRepeat<T> {
        return RangeResultRuleDefaultRepeat(
            rule = core.rule.clone() as RuleWithDefaultRepeat<T>,
            outRange = outRange,
            name = name
        )
    }

    override fun name(name: String): Rule<T> {
        return RangeResultRuleDefaultRepeat(rule = core.rule, outRange, name)
    }
}