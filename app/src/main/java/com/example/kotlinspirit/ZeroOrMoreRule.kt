package com.example.kotlinspirit

import java.lang.IllegalStateException

class ZeroOrMoreRule<T : Any>(
    private val rule: Rule<T>
) : BaseRule<List<T>>() {
    private val result = ArrayList<T>()

    override fun parse(seek: Int, string: CharSequence): Int {
        var i = seek
        while (i < string.length) {
            val seekBefore = i
            i = rule.parse(i, string)
            if (i < 0) {
                return seekBefore
            }
        }

        return i
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<List<T>>) {
        var i = seek
        val list = ArrayList<T>()
        val itemResult = ParseResult<T>()
        result.data = list
        while (i < string.length) {
            val seekBefore = i
            rule.parseWithResult(i, string, itemResult)
            i = itemResult.errorCodeOrSeek
            if (i < 0) {
                result.errorCodeOrSeek = seekBefore
            } else {
                list.add(itemResult.data ?: throw IllegalStateException("data should not be empty"))
            }
        }

        result.errorCodeOrSeek = i
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
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
                notifyParseStepComplete(string)
                return createStepResult(
                    seek = stepRes.getSeek(),
                    stepCode = StepCode.COMPLETE
                )
            }
            stepCode == StepCode.COMPLETE -> {
                result.add(rule.getStepParserResult(string))
                rule.resetStep()
                return createStepResult(
                    seek = stepRes.getSeek(),
                    stepCode = StepCode.MAY_COMPLETE
                )
            }
            else -> {
                return stepRes
            }
        }
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return rule.noParse(seek, string)
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        return rule.noParseStep(seek, string)
    }

    override fun resetNoStep() {
        rule.resetNoStep()
    }

    override fun clone(): ZeroOrMoreRule<T> {
        return ZeroOrMoreRule(
            rule = rule.clone()
        )
    }
}