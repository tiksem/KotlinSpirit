package com.kotlinspirit.repeat

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.getSeek
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import java.lang.IllegalStateException

class ZeroOrMoreRule<T : Any>(
    private val rule: Rule<T>,
    name: String? = null
) : RuleWithDefaultRepeat<List<T>>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        var i = seek
        while (i < string.length) {
            val seekBefore = i
            val ruleRes = rule.parse(i, string)
            if (ruleRes.getParseCode().isError()) {
                return createComplete(seekBefore)
            } else {
                i = ruleRes.getSeek()
                if (i == seekBefore) {
                    return createComplete(i)
                }
            }
        }

        return createComplete(i)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<List<T>>) {
        var i = seek
        val list = ArrayList<T>()
        val itemResult = ParseResult<T>()
        result.data = list
        while (i < string.length) {
            val seekBefore = i
            rule.parseWithResult(i, string, itemResult)
            val stepResult = itemResult.parseResult
            if (stepResult.getParseCode().isError()) {
                result.parseResult = createComplete(seekBefore)
                return
            } else {
                i = stepResult.getSeek()
                if (i == seekBefore) {
                    result.parseResult = createComplete(i)
                    return
                }

                list.add(itemResult.data ?: throw IllegalStateException("data should not be empty"))
            }
        }

        result.parseResult = createComplete(i)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun clone(): ZeroOrMoreRule<T> {
        return ZeroOrMoreRule(rule = rule.clone(), name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(engine: DebugEngine): DebugRule<List<T>> {
        return DebugRule(
            rule = ZeroOrMoreRule(rule.debug(engine), name),
            engine = engine
        )
    }

    override fun name(name: String): ZeroOrMoreRule<T> {
        return ZeroOrMoreRule(rule, name)
    }

    override val defaultDebugName: String
        get() = "*${rule.wrappedName}"

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun isDynamic(): Boolean {
        return rule.isDynamic()
    }

    override fun ignoreCallbacks(): ZeroOrMoreRule<T> {
        return ZeroOrMoreRule(rule.ignoreCallbacks())
    }
}