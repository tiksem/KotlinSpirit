package com.kotlinspirit.repeat

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.ext.debugString

class RepeatRule<T : Any>(
    private val rule: Rule<T>,
    private val range: IntRange,
    name: String? = null
) : RuleWithDefaultRepeat<List<T>>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        var i = seek
        var resultsCount = 0
        while (i < string.length && resultsCount < range.last) {
            val seekBefore = i
            val ruleRes = rule.parse(i, string)
            if (ruleRes.getParseCode().isError()) {
                return if (resultsCount < range.first) {
                    ruleRes
                } else {
                    createComplete(seekBefore)
                }
            } else {
                i = ruleRes.getSeek()
                resultsCount++
            }
        }

        return if (resultsCount >= range.first) {
            createComplete(i)
        } else {
            return createStepResult(
                seek = i,
                parseCode = ParseCode.EOF
            )
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<List<T>>) {
        val i = seek
        val list = ArrayList<T>()
        val itemResult = ParseResult<T>()
        while (i < string.length  && list.size < range.last) {
            val seekBefore = i
            rule.parseWithResult(seek, string, itemResult)
            val stepResult = itemResult.parseResult
            if (stepResult.getParseCode().isError()) {
                if (list.size >= range.first) {
                    result.data = list
                    result.parseResult = createComplete(seekBefore)
                } else {
                    result.parseResult = stepResult
                }
            } else {
                list.add(itemResult.data ?: continue)
            }
        }

        if (list.size >= range.first) {
            result.data = list
            result.parseResult = createComplete(i)
        } else {
            result.parseResult = createStepResult(
                seek = i,
                parseCode = ParseCode.EOF
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        repeat (range.first) {
            if (rule.parse(seek, string).getParseCode().isError()) {
                return false
            }
        }

        return true
    }

    override fun not(): Rule<*> {
        return when {
            range.first <= 0 -> {
                !ZeroOrMoreRule(rule)
            }
            range.first == 1 -> {
                !OneOrMoreRule(rule)
            }
            else -> {
                RepeatRule(rule, 0 until range.first) + !ZeroOrMoreRule(rule)
            }
        }
    }

    override fun clone(): RepeatRule<T> {
        return RepeatRule(rule.clone(), range, name)
    }

    override fun debug(engine: DebugEngine): DebugRule<List<T>> {
        return DebugRule(
            rule = RepeatRule(rule.debug(engine), range, name),
            engine = engine
        )
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): RepeatRule<T> {
        return RepeatRule(rule, range, name)
    }

    override val defaultDebugName: String
        get() = "${rule.wrappedName}.repeat(${range.debugString})"

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun isDynamic(): Boolean {
        return rule.isDynamic()
    }

    override fun ignoreCallbacks(): RepeatRule<T> {
        return RepeatRule(rule.ignoreCallbacks(), range)
    }
}