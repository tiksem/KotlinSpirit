package com.kotlinspirit.rangeres.callbacks

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.base.BaseRangeResultDefaultRepeatRule
import com.kotlinspirit.rangeres.core.RangeResultRuleCallbacksCore
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

internal open class RangeResultCallbacksRuleDefaultRepeat<T : Any>(
    rule: Rule<T>,
    internal val callback: (ParseRange) -> Unit
) : BaseRangeResultDefaultRepeatRule<T>(
    core = RangeResultRuleCallbacksCore(rule, callback)
) {
    override fun clone(): RuleWithDefaultRepeat<T> {
        return RangeResultCallbacksRuleDefaultRepeat(
            rule = core.rule.clone(),
            callback = callback
        )
    }

    override fun debug(name: String?): RuleWithDefaultRepeat<T> {
        return DebugRangeResultCallbacksRuleDefaultRepeat(
            name = name ?: "rangeResult",
            rule = core.rule.internalDebug(),
            callback = callback
        )
    }
}

private class DebugRangeResultCallbacksRuleDefaultRepeat<T : Any>(
    override val name: String,
    rule: Rule<T>,
    callback: (ParseRange) -> Unit
) : RangeResultCallbacksRuleDefaultRepeat<T>(rule, callback), DebugRule {
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

    override fun clone(): DebugRangeResultCallbacksRuleDefaultRepeat<T> {
        return DebugRangeResultCallbacksRuleDefaultRepeat(
            name = name,
            rule = core.rule.clone() as RuleWithDefaultRepeat<T>,
            callback = callback
        )
    }
}