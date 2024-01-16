package com.kotlinspirit.group

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class GroupRule<T : Any> private constructor(
    name: String? = null,
    private val rules: List<Rule<*>>,
    private val resultIndex: Int
) : RuleWithDefaultRepeat<T>(name) {

    constructor(name: String? = null, rules: List<Rule<*>>) : this(
        name = name,
        rules = rules,
        resultIndex = (rules.indexOfFirst {
            it is GroupResultRule<*>
        }.takeIf { it >= 0 } ?: throw IllegalStateException("result in group not found")).takeIf {
            rules.indexOfLast {
                it is GroupResultRule<*>
            } == it
        } ?: throw IllegalStateException("Group could contain only one result rule")
    )

    override fun clone(): GroupRule<T> {
        return GroupRule(name, rules.map {
            it.clone()
        }, resultIndex)
    }

    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        var i = seek
        for (rule in rules) {
            val result = rule.parse(i, string)
            if (result.isError) {
                return result
            }
            i = result.seek
        }

        return ParseSeekResult(i)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        var i = seek
        for (ruleIndex in 0 until rules.size - 1) {
            val rule = rules[ruleIndex]
            val result = rule.parse(i, string)
            if (result.isError) {
                return false
            }
            i = result.seek
        }

        return rules.last().hasMatch(i, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        var i = seek
        for (rule in rules.asReversed()) {
            val result = rule.reverseParse(i, string)
            if (result.isError) {
                return result
            }
            i = result.seek
        }

        return ParseSeekResult(i)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        var i = seek
        for (ruleIndex in rules.size - 1 downTo  1) {
            val rule = rules[ruleIndex]
            val result = rule.parse(i, string)
            if (result.isError) {
                return false
            }
            i = result.seek
        }

        return rules.first().hasMatch(i, string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        var i = seek
        for (ruleIndex in rules.size - 1 downTo  0) {
            val rule = rules[ruleIndex]
            if (ruleIndex == resultIndex) {
                (rule as Rule<T>).reverseParseWithResult(seek, string, result)
                if (result.isError) {
                    return
                }
                i = result.endSeek
            } else {
                val r = rule.reverseParse(i, string)
                if (r.isError) {
                    result.data = null
                    result.parseResult = r
                    return
                }
                i = r.seek
            }
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        var i = seek
        for (ruleIndex in rules.indices) {
            val rule = rules[ruleIndex]
            if (ruleIndex == resultIndex) {
                (rule as Rule<T>).parseWithResult(seek, string, result)
                if (result.isError) {
                    return
                }
                i = result.endSeek
            } else {
                val r = rule.parse(i, string)
                if (r.isError) {
                    result.data = null
                    result.parseResult = r
                    return
                }
                i = r.seek
            }
        }
    }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun name(name: String): Rule<T> {
        return GroupRule(name, rules, resultIndex)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "group(${rules.joinToString(",") { it.debugName }})"
}