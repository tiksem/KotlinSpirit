package com.kotlinspirit.safe

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.platform.createThreadLocal
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class ThreadSafeRule<T : Any>(originalRule: Rule<T>) : RuleWithDefaultRepeat<T>(null) {
    private val rule = createThreadLocal { originalRule.clone() }

    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return rule.get().parse(seek, string)
    }

    override fun clone(): RuleWithDefaultRepeat<T> {
        return ThreadSafeRule(rule.get().clone())
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.get().parseWithResult(seek, string, result)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.get().hasMatch(seek, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return rule.get().reverseParse(seek, string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.get().reverseParseWithResult(seek, string, result)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.get().reverseHasMatch(seek, string)
    }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun name(name: String): Rule<T> {
        return ThreadSafeRule(rule.get().name(name))
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = rule.get().debugNameShouldBeWrapped

    override val defaultDebugName: String
        get() = rule.get().defaultDebugName
}