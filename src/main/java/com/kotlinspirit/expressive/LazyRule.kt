package com.kotlinspirit.expressive

import com.kotlinspirit.char.CharPredicateRule
import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.BaseRuleWithResult
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
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.repeat.RuleWithDefaultRepeatResult
import com.kotlinspirit.str.StringCharPredicateRangeRule
import com.kotlinspirit.str.StringCharPredicateRule
import com.kotlinspirit.str.StringOneOrMoreCharPredicateRule

abstract class BaseLazyRule<T : Any>(
    protected val ruleProvider: () -> Rule<T>
): Rule<T>() {
    private var rule: Rule<T>? = null

    protected open fun initRule(): Rule<T> {
        val rule = this.rule
        if (rule != null) {
            return rule
        }

        return ruleProvider().also {
            this.rule = it
        }
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        return initRule().parse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        initRule().parseWithResult(seek, string, result)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return initRule().hasMatch(seek, string)
    }

    override fun isThreadSafe(): Boolean {
        return false
    }
}

class LazyCharPredicateRule(
    ruleProvider: () -> CharPredicateRule
) : BaseLazyRule<Char>(ruleProvider) {
    override fun repeat(): StringCharPredicateRule {
        return StringCharPredicateRule((initRule() as CharPredicateRule).predicate)
    }

    override fun repeat(range: IntRange): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule((initRule() as CharPredicateRule).predicate, range)
    }

    override fun unaryPlus(): StringOneOrMoreCharPredicateRule {
        return StringOneOrMoreCharPredicateRule((initRule() as CharPredicateRule).predicate)
    }

    override fun invoke(callback: (Char) -> Unit): BaseRuleWithResult<Char> {
        return initRule().invoke(callback)
    }

    override fun clone(): LazyCharPredicateRule {
        return this
    }

    override fun ignoreCallbacks(): LazyCharPredicateRule {
        return LazyCharPredicateRule {
            ruleProvider().ignoreCallbacks() as CharPredicateRule
        }
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): LazyCharPredicateRule {
        return LazyCharPredicateRule {
            ruleProvider().debug(name ?: "lazy") as CharPredicateRule
        }
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

open class LazyRule<T : Any>(
    ruleProvider: () -> RuleWithDefaultRepeat<T>
) : BaseLazyRule<T>(ruleProvider) {
    override fun initRule(): RuleWithDefaultRepeat<T> {
        return super.initRule() as RuleWithDefaultRepeat<T>
    }

    override fun repeat(): Rule<List<T>> {
        return initRule().repeat()
    }

    override fun repeat(range: IntRange): Rule<List<T>> {
        return initRule().repeat(range)
    }

    override fun unaryPlus(): Rule<List<T>> {
        return +initRule()
    }

    override fun invoke(callback: (T) -> Unit): BaseRuleWithResult<T> {
        return RuleWithDefaultRepeatResult(initRule(), callback)
    }

    override fun clone(): LazyRule<T> {
        return this
    }

    override fun ignoreCallbacks(): LazyRule<T> {
        return LazyRule {
            ruleProvider().ignoreCallbacks() as RuleWithDefaultRepeat<T>
        }
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override fun debug(name: String?): LazyRule<T> {
        return DebugLazyRule(
            name = name ?: "lazy",
            ruleProvider = {
                ruleProvider().internalDebug() as RuleWithDefaultRepeat<T>
            }
        )
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

private class DebugLazyRule<T : Any>(
    override val name: String,
    ruleProvider: () -> RuleWithDefaultRepeat<T>
): LazyRule<T>(ruleProvider), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}