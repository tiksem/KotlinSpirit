package com.kotlinspirit.repeat

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import java.lang.IllegalStateException

open class OneOrMoreRule<T : Any>(
    protected val rule: Rule<T>
) : RuleWithDefaultRepeat<List<T>>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        var i = seek
        var success = false
        while (i < string.length) {
            val seekBefore = i
            val ruleRes = rule.parse(i, string)
            if (ruleRes.getParseCode().isError()) {
                return if (success) {
                    createComplete(seekBefore)
                } else {
                    ruleRes
                }
            } else {
                i = ruleRes.getSeek()
                success = true
            }
        }

        return if (success) {
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
        while (i < string.length) {
            val seekBefore = i
            rule.parseWithResult(i, string, itemResult)
            val stepResult = itemResult.parseResult
            if (stepResult.getParseCode().isError()) {
                if (list.isNotEmpty()) {
                    result.data = list
                    result.parseResult = createComplete(seekBefore)
                } else {
                    result.parseResult = stepResult
                }
                return
            } else {
                list.add(itemResult.data ?: throw IllegalStateException("data should not be empty"))
                i = stepResult.getSeek()
            }
        }

        if (list.isNotEmpty()) {
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
        return rule.hasMatch(seek, string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return rule.noParse(seek, string)
    }

    override fun clone(): OneOrMoreRule<T> {
        return OneOrMoreRule(rule.clone())
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): OneOrMoreRule<T> {
        val debug = rule.internalDebug()
        return DebugOneOrMoreRule(
            name ?: "${debug.debugNameWrapIfNeed}.repeat(1..<)",
            debug
        )
    }

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun ignoreCallbacks(): OneOrMoreRule<T> {
        return OneOrMoreRule(rule.ignoreCallbacks())
    }
}

private class DebugOneOrMoreRule<T : Any>(
    override val name: String,
    rule: Rule<T>
) : OneOrMoreRule<T>(rule), DebugRule {
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

    override fun clone(): OneOrMoreRule<T> {
        return DebugOneOrMoreRule(name, rule.clone())
    }
}