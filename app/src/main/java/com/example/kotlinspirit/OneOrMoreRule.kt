package com.example.kotlinspirit

import java.lang.IllegalStateException

class OneOrMoreRule<T : Any>(
    private val rule: Rule<T>
) : RuleWithDefaultRepeat<List<T>>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        var i = seek
        var success = false
        while (i < string.length) {
            val seekBefore = i
            val ruleRes = rule.parse(seek, string)
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
            rule.parseWithResult(seek, string, itemResult)
            val stepResult = itemResult.stepResult
            if (stepResult.getParseCode().isError()) {
                if (list.isNotEmpty()) {
                    result.data = list
                    result.stepResult = createComplete(seekBefore)
                } else {
                    result.stepResult = stepResult
                }
                return
            } else {
                list.add(itemResult.data ?: throw IllegalStateException("data should not be empty"))
                i = stepResult.getSeek()
            }
        }

        if (list.isNotEmpty()) {
            result.data = list
            result.stepResult = createComplete(i)
        } else {
            result.stepResult = createStepResult(
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
        return OneOrMoreRule(
            rule = rule.clone()
        )
    }
}