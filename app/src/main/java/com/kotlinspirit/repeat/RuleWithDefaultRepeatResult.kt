package com.kotlinspirit.repeat

import com.kotlinspirit.char.CharPredicateRule
import com.kotlinspirit.core.BaseRuleWithResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.str.StringCharPredicateRangeRule
import com.kotlinspirit.str.StringCharPredicateRule
import com.kotlinspirit.str.StringOneOrMoreCharPredicateRule

open class RuleWithDefaultRepeatResult<T : Any>(
    rule: RuleWithDefaultRepeat<T>,
    callback: (T) -> Unit
) : BaseRuleWithResult<T>(rule, callback) {
    override fun repeat(): Rule<List<T>> {
        return rule.repeat() as Rule<List<T>>
    }

    override fun repeat(range: IntRange): Rule<List<T>> {
        return rule.repeat(range) as Rule<List<T>>
    }

    override fun unaryPlus(): Rule<List<T>> {
        return +rule as Rule<List<T>>
    }

    override fun invoke(callback: (T) -> Unit): RuleWithDefaultRepeatResult<T> {
        return RuleWithDefaultRepeatResult(rule as RuleWithDefaultRepeat<T>, callback)
    }

    override fun clone(): Rule<T> {
        return RuleWithDefaultRepeatResult((rule as RuleWithDefaultRepeat<T>).clone(), callback)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = rule.debugNameShouldBeWrapped

    override fun debug(name: String?): RuleWithDefaultRepeatResult<T> {
        return DebugRuleWithDefaultRepeatResult(rule.debug(name) as RuleWithDefaultRepeat<T>, callback)
    }
}

private class DebugRuleWithDefaultRepeatResult<T : Any>(
    rule: RuleWithDefaultRepeat<T>,
    callback: (T) -> Unit
) : RuleWithDefaultRepeatResult<T>(rule, callback), DebugRule {
    override val name: String
        get() = rule.debugName
}

open class CharPredicateResultRule(
    rule: CharPredicateRule,
    callback: (Char) -> Unit
) : BaseRuleWithResult<Char>(rule, callback) {
    override fun repeat(): StringCharPredicateRule {
        return (rule as CharPredicateRule).repeat()
    }

    override fun repeat(range: IntRange): StringCharPredicateRangeRule {
        return (rule as CharPredicateRule).repeat(range)
    }

    override fun unaryPlus(): StringOneOrMoreCharPredicateRule {
        return +(rule as CharPredicateRule)
    }

    override fun invoke(callback: (Char) -> Unit): CharPredicateResultRule {
        return CharPredicateResultRule(rule as CharPredicateRule, callback)
    }

    override fun not(): CharPredicateRule {
        val rule = rule as CharPredicateRule
        return CharPredicateRule(
            predicate = {
                !rule.predicate(it)
            }
        )
    }

    override fun clone(): CharPredicateResultRule {
        return CharPredicateResultRule((rule as CharPredicateRule).clone(), callback)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = rule.debugNameShouldBeWrapped

    override fun debug(name: String?): CharPredicateResultRule {
        return DebugCharPredicateResultRule(rule.debug(name) as CharPredicateRule, callback)
    }
}

private class DebugCharPredicateResultRule(
    rule: CharPredicateRule,
    callback: (Char) -> Unit
) : CharPredicateResultRule(rule, callback), DebugRule {
    override val name: String
        get() = rule.debugName
}