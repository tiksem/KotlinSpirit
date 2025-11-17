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
    private val out: ParseRangeResult<Char>,
    name: String? = null
) : BaseRangeResultCharRule(
    core = RangeResultGetRangeResultCore(rule, out),
    name
) {
    override fun clone(): RangeResultCharResultRule {
        return RangeResultCharResultRule(
            rule = core.rule.clone(),
            out = out,
            name = name
        )
    }

    override fun name(name: String): RangeResultCharResultRule {
        return RangeResultCharResultRule(rule = core.rule, out, name)
    }

    override fun debug(engine: DebugEngine): DebugRule<Char> {
        return DebugRule(
            rule = RangeResultCharResultRule(
                rule = core.rule.debug(engine),
                out, name
            ),
            engine = engine
        )
    }
}