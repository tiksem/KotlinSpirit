package com.example.kotlinspirit

import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException

class RepeatRule<T : Any>(
    private val rule: Rule<T>,
    private val range: IntRange
) : RuleWithDefaultRepeat<List<T>>() {
    private val result = ArrayList<T>()

    override fun parse(seek: Int, string: CharSequence): Long {
        var i = seek
        var resultsCount = 0
        while (i < string.length && resultsCount < range.last) {
            val seekBefore = i
            val ruleRes = rule.parse(seek, string)
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
            val stepResult = itemResult.stepResult
            if (stepResult.getParseCode().isError()) {
                if (list.size >= range.first) {
                    result.data = list
                    result.stepResult = createComplete(seekBefore)
                } else {
                    result.stepResult = stepResult
                }
            } else {
                list.add(itemResult.data ?: throw IllegalStateException("data should not be empty"))
            }
        }

        if (list.size >= range.first) {
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
        repeat (range.first) {
            if (rule.parse(seek, string) < 0) {
                return false
            }
        }

        return true
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        throw UnsupportedOperationException()
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
        return RepeatRule(
            rule = rule.clone(),
            range = range
        )
    }
}