package com.example.kotlinspirit

import java.lang.IllegalStateException

open class ZeroOrMoreRule<T : Any>(
    protected val rule: Rule<T>
) : RuleWithDefaultRepeat<List<T>>() {
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

    override fun noParse(seek: Int, string: CharSequence): Int {
        return -seek-1
    }

    override fun clone(): ZeroOrMoreRule<T> {
        return ZeroOrMoreRule(rule = rule.clone())
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): ZeroOrMoreRule<T> {
        val debug = rule.internalDebug()
        return DebugZeroOrMoreRule(
            name = name ?: "${debug.debugNameWrapIfNeed}.repeat(0..<)",
            rule = debug
        )
    }
}

private class DebugZeroOrMoreRule<T : Any>(
    override val name: String,
    rule: Rule<T>
) : ZeroOrMoreRule<T>(rule), DebugRule {
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

    override fun clone(): ZeroOrMoreRule<T> {
        return DebugZeroOrMoreRule(name, rule.clone())
    }
}