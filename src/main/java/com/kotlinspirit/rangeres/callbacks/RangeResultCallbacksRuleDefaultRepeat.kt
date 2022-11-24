package com.kotlinspirit.rangeres.callbacks

import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.base.BaseRangeResultDefaultRepeatRule
import com.kotlinspirit.rangeres.core.RangeResultRuleCallbacksCore
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

internal open class RangeResultCallbacksRuleDefaultRepeat<T : Any>(
    rule: Rule<T>,
    internal val callback: (ParseRange) -> Unit,
    name: String? = null
) : BaseRangeResultDefaultRepeatRule<T>(
    core = RangeResultRuleCallbacksCore(rule, callback),
    name
) {
    override fun clone(): RuleWithDefaultRepeat<T> {
        return RangeResultCallbacksRuleDefaultRepeat(
            rule = core.rule.clone(),
            callback = callback,
            name = name
        )
    }

    override fun name(name: String): RangeResultCallbacksRuleDefaultRepeat<T> {
        return RangeResultCallbacksRuleDefaultRepeat(rule = core.rule, callback = callback, name = name)
    }

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = RangeResultCallbacksRuleDefaultRepeat(
                rule = core.rule.debug(engine),
                callback = callback,
                name = name
            ),
            engine = engine
        )
    }
}