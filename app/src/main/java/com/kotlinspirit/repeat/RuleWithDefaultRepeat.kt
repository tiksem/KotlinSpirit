package com.kotlinspirit.repeat

import com.kotlinspirit.core.Rule

abstract class RuleWithDefaultRepeat<T : Any> : Rule<T>() {
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

    abstract override fun clone(): RuleWithDefaultRepeat<T>
    abstract override fun debug(name: String?): RuleWithDefaultRepeat<T>
}