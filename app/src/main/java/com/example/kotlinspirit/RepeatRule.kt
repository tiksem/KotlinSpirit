package com.example.kotlinspirit

import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException

class RepeatRule<T : Any>(
    private val rule: Rule<T>,
    private val range: IntRange
) : BaseRule<List<T>>() {
    private val result = ArrayList<T>()

    override fun parse(seek: Int, string: CharSequence): Int {
        var i = seek
        var resultsCount = 0
        while (i < string.length && resultsCount < range.last) {
            val seekBefore = i
            i = rule.parse(seek, string)
            if (i < 0) {
                return if (resultsCount < range.first) {
                    i
                } else {
                    seekBefore
                }
            } else {
                resultsCount++
            }
        }

        return if (resultsCount >= range.first) {
            i
        } else {
            return -StepCode.EOF
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<List<T>>) {
        var i = seek
        val list = ArrayList<T>()
        val itemResult = ParseResult<T>()
        while (i < string.length  && list.size < range.last) {
            val seekBefore = i
            rule.parseWithResult(seek, string, itemResult)
            i = itemResult.errorCodeOrSeek
            if (i < 0) {
                if (list.size >= range.first) {
                    result.data = list
                    result.errorCodeOrSeek = seekBefore
                } else {
                    result.errorCodeOrSeek = i
                }
            } else {
                list.add(itemResult.data ?: throw IllegalStateException("data should not be empty"))
            }
        }

        if (list.size >= range.first) {
            result.data = list
            result.errorCodeOrSeek = i
        } else {
            result.errorCodeOrSeek = -StepCode.EOF
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

    override fun resetStep() {
        rule.resetStep()
        result.clear()
    }

    override fun getStepParserResult(string: CharSequence): List<T> {
        return result
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        val stepRes = rule.parseStep(seek, string)
        val stepCode = stepRes.getStepCode()
        when {
            stepCode.isError() -> {
                return if (result.size >= range.first) {
                    notifyParseStepComplete(string)
                    createStepResult(
                        seek = stepRes.getSeek(),
                        stepCode = StepCode.COMPLETE
                    )
                } else {
                    stepRes
                }
            }
            stepCode == StepCode.COMPLETE -> {
                result.add(rule.getStepParserResult(string))
                if (result.size == range.last) {
                    notifyParseStepComplete(string)
                    return createStepResult(
                        seek = stepRes.getSeek(),
                        stepCode = StepCode.COMPLETE
                    )
                }

                rule.resetStep()
                return createStepResult(
                    seek = stepRes.getSeek(),
                    stepCode = if (result.size >= range.first) {
                        StepCode.MAY_COMPLETE
                    } else {
                        StepCode.HAS_NEXT
                    }
                )
            }
            else -> {
                return stepRes
            }
        }
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        throw UnsupportedOperationException()
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        throw UnsupportedOperationException()
    }

    override fun resetNoStep() {
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