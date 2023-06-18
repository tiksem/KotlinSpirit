package com.kotlinspirit.expressive

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.rangeres.callbacks.RangeResultCallbacksRuleDefaultRepeat
import com.kotlinspirit.rangeres.result.RangeResultRuleCallbacksResultDefaultRepeat
import com.kotlinspirit.rangeres.result.RangeResultRuleResultDefaultRepeat
import com.kotlinspirit.rangeres.simple.RangeResultRuleDefaultRepeat
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class LazyRule<T : Any> internal constructor(
    private val ruleProvider: () -> Rule<T>,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {
    private var rule: Rule<T>? = null

    private fun initRule(): Rule<T> {
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

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return initRule().reverseParse(seek, string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        initRule().reverseParseWithResult(seek, string, result)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return initRule().reverseHasMatch(seek, string)
    }

    override fun isThreadSafe(): Boolean {
        return false
    }

    override val defaultDebugName: String
        get() = "lazy"

    override fun clone(): LazyRule<T> {
        return this
    }

    override fun name(name: String): LazyRule<T> {
        return LazyRule(ruleProvider = ruleProvider, name = name)
    }

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = LazyRule(
                ruleProvider = {
                    ruleProvider().debug(engine)
                }, name
            ),
            engine = engine
        )
    }

    override fun ignoreCallbacks(): LazyRule<T> {
        return LazyRule(ruleProvider = {
            ruleProvider().ignoreCallbacks() as RuleWithDefaultRepeat<T>
        }, name = name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

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