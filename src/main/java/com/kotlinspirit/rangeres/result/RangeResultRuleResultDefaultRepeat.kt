package com.kotlinspirit.rangeres.result

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.rangeres.base.BaseRangeResultDefaultRepeatRule
import com.kotlinspirit.rangeres.core.RangeResultGetRangeResultCore
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

internal open class RangeResultRuleResultDefaultRepeat<T : Any>(
    rule: Rule<T>,
    protected val out: ParseRangeResult<T>
) : BaseRangeResultDefaultRepeatRule<T>(
    core = RangeResultGetRangeResultCore(rule, out)
) {
    override fun clone(): RangeResultRuleResultDefaultRepeat<T> {
        return RangeResultRuleResultDefaultRepeat(
            rule = core.rule.clone(),
            out = out
        )
    }

    override fun debug(name: String?): RangeResultRuleResultDefaultRepeat<T> {
        return DebugRangeResultRuleResultDefaultRepeat(
            name = "rangeResult",
            rule = core.rule.internalDebug(),
            out = out
        )
    }
}

private class DebugRangeResultRuleResultDefaultRepeat<T : Any>(
    override val name: String,
    rule: Rule<T>,
    out: ParseRangeResult<T>
) : RangeResultRuleResultDefaultRepeat<T>(rule, out), DebugRule {
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

    override fun clone(): DebugRangeResultRuleResultDefaultRepeat<T> {
        return DebugRangeResultRuleResultDefaultRepeat(
            name = name,
            rule = core.rule.clone() as RuleWithDefaultRepeat<T>,
            out = out
        )
    }
}