package com.kotlinspirit.rangeres.simple

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.base.BaseRangeResultDefaultRepeatRule
import com.kotlinspirit.rangeres.core.RangeResultRuleGetRangeCore
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

internal open class RangeResultRuleDefaultRepeat<T : Any>(
    rule: Rule<T>,
    internal val outRange: ParseRange
) : BaseRangeResultDefaultRepeatRule<T>(
    core = RangeResultRuleGetRangeCore<T>(rule, outRange)
) {
    override fun clone(): RangeResultRuleDefaultRepeat<T> {
        return RangeResultRuleDefaultRepeat(
            rule = core.rule.clone() as RuleWithDefaultRepeat<T>,
            outRange = outRange
        )
    }

    override fun debug(name: String?): RangeResultRuleDefaultRepeat<T> {
        return DebugRangeResultRuleDefaultRepeat(
            name = name ?: "rangeResult",
            rule = core.rule.internalDebug(),
            outRange = outRange
        )
    }
}

private class DebugRangeResultRuleDefaultRepeat<T : Any>(
    override val name: String,
    rule: Rule<T>,
    outRange: ParseRange
) : RangeResultRuleDefaultRepeat<T>(rule, outRange), DebugRule {
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

    override fun clone(): DebugRangeResultRuleDefaultRepeat<T> {
        return DebugRangeResultRuleDefaultRepeat(
            name = name,
            rule = core.rule.clone() as RuleWithDefaultRepeat<T>,
            outRange = outRange
        )
    }
}