package com.kotlinspirit.result

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class GroupSequenceRule<T : Any>(
    private val rule: ResultSequenceRule<T>,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {
    override fun clone(): GroupSequenceRule<T> {
        return GroupSequenceRule(rule.clone(), name)
    }

    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return rule.parse(seek, string)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return rule.reverseParse(seek, string)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.reverseHasMatch(seek, string)
    }

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun name(name: String): GroupSequenceRule<T> {
        return GroupSequenceRule(rule = rule, name = name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "group(${rule.debugName})"

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.reverseParseWithResult(seek, string, result)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
    }
}