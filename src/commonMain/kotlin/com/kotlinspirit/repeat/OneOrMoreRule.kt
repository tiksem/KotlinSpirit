package com.kotlinspirit.repeat

import com.kotlinspirit.core.*
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule

class OneOrMoreRule<T : Any>(
    private val rule: Rule<T>,
    name: String? = null
) : RuleWithDefaultRepeat<List<T>>(name) {
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

    override fun clone(): OneOrMoreRule<T> {
        return OneOrMoreRule(rule.clone(), name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(engine: DebugEngine): DebugRule<List<T>> {
        return DebugRule(
            rule = OneOrMoreRule(rule.debug(engine), name),
            engine = engine
        )
    }

    override fun name(name: String): OneOrMoreRule<T> {
        return OneOrMoreRule(rule, name)
    }

    override val defaultDebugName: String
        get() = "+${rule.wrappedName}"

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun ignoreCallbacks(): OneOrMoreRule<T> {
        return OneOrMoreRule(rule.ignoreCallbacks(), name)
    }
}