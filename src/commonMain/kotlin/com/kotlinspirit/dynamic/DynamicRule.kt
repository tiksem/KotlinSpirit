package com.kotlinspirit.dynamic

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class DynamicRule<T : Any>(
    name: String? = null,
    private val ruleFactory: () -> Rule<T>
) : RuleWithDefaultRepeat<T>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return ruleFactory().parse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        ruleFactory().parseWithResult(seek, string, result)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return ruleFactory().hasMatch(seek, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return ruleFactory().reverseParse(seek, string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        ruleFactory().reverseParseWithResult(seek, string, result)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return ruleFactory().reverseHasMatch(seek, string)
    }

    override fun isThreadSafe(): Boolean {
        return false
    }

    override fun name(name: String): DynamicRule<T> {
        return DynamicRule(name, ruleFactory)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "dynamic(${ruleFactory().defaultDebugName})"

    override fun clone(): DynamicRule<T> {
        return this
    }
}