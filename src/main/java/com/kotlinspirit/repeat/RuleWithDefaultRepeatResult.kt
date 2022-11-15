package com.kotlinspirit.repeat

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
        return DebugRuleWithDefaultRepeatResult(
            name = name ?: "result",
            rule = rule.internalDebug() as RuleWithDefaultRepeat<T>, callback
        )
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

private class DebugRuleWithDefaultRepeatResult<T : Any>(
    override val name: String,
    rule: RuleWithDefaultRepeat<T>,
    callback: (T) -> Unit
) : RuleWithDefaultRepeatResult<T>(rule, callback), DebugRule {
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

    override fun clone(): RuleWithDefaultRepeatResult<T> {
        return DebugRuleWithDefaultRepeatResult(name, rule.clone() as RuleWithDefaultRepeat<T>, callback)
    }
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
        get() = false

    override fun debug(name: String?): CharPredicateResultRule {
        return DebugCharPredicateResultRule(name ?: "result", rule.internalDebug() as CharPredicateRule, callback)
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

private class DebugCharPredicateResultRule(
    override val name: String,
    rule: CharPredicateRule,
    callback: (Char) -> Unit
) : CharPredicateResultRule(rule, callback), DebugRule {
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

    override fun clone(): CharPredicateResultRule {
        return DebugCharPredicateResultRule(name, rule.clone() as CharPredicateRule, callback)
    }
}