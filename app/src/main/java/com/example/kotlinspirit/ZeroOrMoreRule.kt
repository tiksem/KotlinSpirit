package com.example.kotlinspirit

import java.lang.IllegalStateException

class ZeroOrMoreRule<T : Any>(
    private val rule: Rule<T>
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
            val stepResult = itemResult.stepResult
            if (stepResult.getParseCode().isError()) {
                result.stepResult = createComplete(seekBefore)
                return
            } else {
                i = stepResult.getSeek()
                if (i == seekBefore) {
                    result.stepResult = createComplete(i)
                    return
                }

                list.add(itemResult.data ?: throw IllegalStateException("data should not be empty"))
            }
        }

        result.stepResult = createComplete(i)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return rule.noParse(seek, string)
    }

    override fun clone(): ZeroOrMoreRule<T> {
        return ZeroOrMoreRule(
            rule = rule.clone()
        )
    }
}