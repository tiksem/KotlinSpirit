package com.kotlinspirit.rangeres.result

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.rangeres.base.BaseRangeResultDefaultRepeatRule
import com.kotlinspirit.rangeres.core.RangeResultGetRangeResultCore
import com.kotlinspirit.rangeres.core.RangeResultRuleResultCallbacksCore
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

internal open class RangeResultRuleCallbacksResultDefaultRepeat<T : Any>(
    rule: Rule<T>,
    protected val callback: (ParseRangeResult<T>) -> Unit
) : BaseRangeResultDefaultRepeatRule<T>(
    core = RangeResultRuleResultCallbacksCore(rule, callback)
) {
    override fun clone(): RangeResultRuleCallbacksResultDefaultRepeat<T> {
        return RangeResultRuleCallbacksResultDefaultRepeat(
            rule = core.rule.clone(),
            callback = callback
        )
    }

    override fun debug(name: String?): RangeResultRuleCallbacksResultDefaultRepeat<T> {
        return DebugRangeResultRuleCallbacksResultDefaultRepeat(
            name = "rangeResult",
            rule = core.rule.internalDebug(),
            callback = callback
        )
    }
}

private class DebugRangeResultRuleCallbacksResultDefaultRepeat<T : Any>(
    override val name: String,
    rule: Rule<T>,
    callback: (ParseRangeResult<T>) -> Unit
) : RangeResultRuleCallbacksResultDefaultRepeat<T>(rule, callback), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<T>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }

    override fun clone(): DebugRangeResultRuleCallbacksResultDefaultRepeat<T> {
        return DebugRangeResultRuleCallbacksResultDefaultRepeat(
            name = name,
            rule = core.rule.clone() as RuleWithDefaultRepeat<T>,
            callback = callback
        )
    }
}