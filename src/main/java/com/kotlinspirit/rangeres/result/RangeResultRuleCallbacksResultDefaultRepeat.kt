package com.kotlinspirit.rangeres.result

import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.rangeres.base.BaseRangeResultDefaultRepeatRule
import com.kotlinspirit.rangeres.core.RangeResultRuleResultCallbacksCore

internal open class RangeResultRuleCallbacksResultDefaultRepeat<T : Any>(
    rule: Rule<T>,
    protected val callback: (ParseRangeResult<T>) -> Unit,
    name: String? = null
) : BaseRangeResultDefaultRepeatRule<T>(
    core = RangeResultRuleResultCallbacksCore(rule, callback),
    name
) {
    override fun clone(): RangeResultRuleCallbacksResultDefaultRepeat<T> {
        return RangeResultRuleCallbacksResultDefaultRepeat(
            rule = core.rule.clone(),
            callback = callback,
            name = name
        )
    }

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = RangeResultRuleCallbacksResultDefaultRepeat(
                rule = core.rule.debug(engine),
                callback = callback,
                name = name
            ),
            engine = engine
        )
    }

    override fun name(name: String): RangeResultRuleCallbacksResultDefaultRepeat<T> {
        return RangeResultRuleCallbacksResultDefaultRepeat(rule = core.rule, callback = callback, name = name)
    }
}