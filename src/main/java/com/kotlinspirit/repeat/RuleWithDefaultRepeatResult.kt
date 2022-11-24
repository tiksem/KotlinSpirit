package com.kotlinspirit.repeat

import com.beust.klaxon.Debug
import com.kotlinspirit.char.CharPredicateRule
import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.BaseRuleWithResult
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.*
import com.kotlinspirit.rangeres.callbacks.RangeResultCallbacksRuleDefaultRepeat
import com.kotlinspirit.rangeres.callbacks.RangeResultCharCallbacksRule
import com.kotlinspirit.rangeres.result.RangeResultCharCallbacksResultRule
import com.kotlinspirit.rangeres.result.RangeResultCharResultRule
import com.kotlinspirit.rangeres.result.RangeResultRuleCallbacksResultDefaultRepeat
import com.kotlinspirit.rangeres.result.RangeResultRuleResultDefaultRepeat
import com.kotlinspirit.rangeres.simple.RangeResultCharRule
import com.kotlinspirit.rangeres.simple.RangeResultRuleDefaultRepeat
import com.kotlinspirit.str.StringCharPredicateRangeRule
import com.kotlinspirit.str.StringCharPredicateRule
import com.kotlinspirit.str.StringOneOrMoreCharPredicateRule

open class RuleWithDefaultRepeatResult<T : Any>(
    rule: Rule<T>,
    callback: (T) -> Unit,
    name: String? = null
) : BaseRuleWithResult<T>(rule, callback, name) {
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

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = RuleWithDefaultRepeatResult(
                rule = rule.debug(engine),
                callback, name
            ),
            engine = engine
        )
    }

    override fun name(name: String): RuleWithDefaultRepeatResult<T> {
        return RuleWithDefaultRepeatResult(rule, callback, name)
    }

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun ignoreCallbacks(): RuleWithDefaultRepeat<T> {
        return rule.ignoreCallbacks() as RuleWithDefaultRepeat<T>
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
}

open class CharPredicateResultRule private constructor(
    rule: Rule<Char>,
    callback: (Char) -> Unit,
    name: String? = null
) : BaseRuleWithResult<Char>(rule, callback, name) {
    internal constructor(
        rule: CharPredicateRule,
        callback: (Char) -> Unit,
        name: String? = null
    ) : this(rule as Rule<Char>, callback, name) {
    }

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
        return CharPredicateResultRule(rule.clone(), callback)
    }

    override fun name(name: String): CharPredicateResultRule {
        return CharPredicateResultRule(rule, callback, name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(engine: DebugEngine): DebugRule<Char> {
        return DebugRule(
            rule = CharPredicateResultRule(
                rule = rule.debug(engine),
                callback, name
            ),
            engine = engine
        )
    }

    override fun ignoreCallbacks(): CharPredicateRule {
        return rule.ignoreCallbacks() as CharPredicateRule
    }

    override fun getRange(out: ParseRange): CharRule {
        return RangeResultCharRule(this, out)
    }

    override fun getRange(callback: (ParseRange) -> Unit): CharRule {
        return RangeResultCharCallbacksRule(this, callback)
    }

    override fun getRangeResult(out: ParseRangeResult<Char>): CharRule {
        return RangeResultCharResultRule(this, out)
    }

    override fun getRangeResult(callback: (ParseRangeResult<Char>) -> Unit): CharRule {
        return RangeResultCharCallbacksResultRule(this, callback)
    }
}