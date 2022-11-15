package com.kotlinspirit.rangeres.result

import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.rangeres.base.BaseRangeResultCharRule
import com.kotlinspirit.rangeres.core.RangeResultGetRangeResultCore

internal open class RangeResultCharResultRule(
    rule: Rule<Char>,
    private val out: ParseRangeResult<Char>
) : BaseRangeResultCharRule(
    core = RangeResultGetRangeResultCore(rule, out)
) {
    override fun clone(): RangeResultCharResultRule {
        return RangeResultCharResultRule(
            rule = core.rule.clone(),
            out = out
        )
    }

    override fun debug(name: String?): CharRule {
        return DebugRangeResultCharResultRule(
            name = name ?: "rangeResult",
            rule = core.rule.internalDebug(),
            out = out
        )
    }
}

private class DebugRangeResultCharResultRule(
    override val name: String,
    rule: Rule<Char>,
    out: ParseRangeResult<Char>
) : RangeResultCharResultRule(rule, out), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<Char>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}