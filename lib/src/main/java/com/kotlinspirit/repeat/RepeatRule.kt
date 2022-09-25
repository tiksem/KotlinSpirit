package com.kotlinspirit.repeat

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import java.lang.UnsupportedOperationException

open class RepeatRule<T : Any>(
    protected val rule: Rule<T>,
    protected val range: IntRange
) : RuleWithDefaultRepeat<List<T>>() {
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
        var i = seek
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
        return RepeatRule(rule.clone(), range)
    }

    override fun debug(name: String?): RepeatRule<T> {
        val debug = rule.internalDebug()
        return DebugRepeatRule(
            name = name ?: "${debug.debugNameWrapIfNeed}.repeat(${range.first}..${range.last})",
            debug, range
        )
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun ignoreCallbacks(): RepeatRule<T> {
        return RepeatRule(rule.ignoreCallbacks(), range)
    }
}

private class DebugRepeatRule<T : Any>(
    override val name: String,
    rule: Rule<T>,
    range: IntRange
): RepeatRule<T>(rule, range), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<List<T>>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }

    override fun clone(): RepeatRule<T> {
        return DebugRepeatRule(name, rule.clone(), range)
    }
}