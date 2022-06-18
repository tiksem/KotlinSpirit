package com.example.kotlinspirit

abstract class BaseRule<T : Any> : Rule<T> {
    override fun repeat(): Rule<List<T>> {
        return ZeroOrMoreRule(this)
    }

    override fun repeat(range: IntRange): Rule<List<T>> {
        return RepeatRule(this, range)
    }

    override fun invoke(callback: (T) -> Unit): RuleWithResult<T> {
        return RuleWithResult(this.clone(), callback)
    }

    abstract override fun clone(): BaseRule<T>
}