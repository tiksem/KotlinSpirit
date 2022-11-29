package com.kotlinspirit.repeat

import com.kotlinspirit.core.Rule
import com.kotlinspirit.hook.ParseRange
import com.kotlinspirit.hook.ParseRangeResult
import com.kotlinspirit.hook.callbacks.RangeResultCallbacksRuleDefaultRepeat
import com.kotlinspirit.hook.result.RangeResultRuleCallbacksResultDefaultRepeat
import com.kotlinspirit.hook.result.RangeResultRuleResultDefaultRepeat
import com.kotlinspirit.hook.simple.RangeResultRuleDefaultRepeat

abstract class RuleWithDefaultRepeat<T : Any>(name: String?) : Rule<T>(name) {
    override fun repeat(): Rule<List<T>> {
        return ZeroOrMoreRule(this)
    }

    override fun repeat(range: IntRange): Rule<List<T>> {
        return RepeatRule(this, range)
    }

    override fun unaryPlus(): Rule<List<T>> {
        return OneOrMoreRule(this)
    }

    override fun invoke(callback: (T) -> Unit): RuleWithDefaultRepeatResult<T> {
        return RuleWithDefaultRepeatResult(this, callback)
    }

    override fun getRange(out: ParseRange): RuleWithDefaultRepeat<T> {
        return RangeResultRuleDefaultRepeat(this, out)
    }

    override fun getRange(callback: (ParseRange) -> Unit): RuleWithDefaultRepeat<T> {
        return RangeResultCallbacksRuleDefaultRepeat(this, callback)
    }

    override fun getRangeResult(out: ParseRangeResult<T>): RuleWithDefaultRepeat<T> {
        return RangeResultRuleResultDefaultRepeat(this, out)
    }

    override fun getRangeResult(callback: (ParseRangeResult<T>) -> Unit): RuleWithDefaultRepeat<T> {
        return RangeResultRuleCallbacksResultDefaultRepeat(this, callback)
    }

    abstract override fun clone(): RuleWithDefaultRepeat<T>
}