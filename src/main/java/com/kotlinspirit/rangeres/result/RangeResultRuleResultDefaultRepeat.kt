package com.kotlinspirit.rangeres.result

import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.rangeres.base.BaseRangeResultDefaultRepeatRule
import com.kotlinspirit.rangeres.core.RangeResultGetRangeResultCore

internal open class RangeResultRuleResultDefaultRepeat<T : Any>(
    rule: Rule<T>,
    protected val out: ParseRangeResult<T>,
    name: String? = null
) : BaseRangeResultDefaultRepeatRule<T>(
    core = RangeResultGetRangeResultCore(rule, out),
    name
) {
    override fun clone(): RangeResultRuleResultDefaultRepeat<T> {
        return RangeResultRuleResultDefaultRepeat(
            rule = core.rule.clone(),
            out = out,
            name = name
        )
    }

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = RangeResultRuleResultDefaultRepeat(
                rule = core.rule.debug(engine),
                out, name
            ),
            engine = engine
        )
    }

    override fun name(name: String): Rule<T> {
        return RangeResultRuleResultDefaultRepeat(rule = core.rule, out, name)
    }
}