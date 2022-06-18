package com.example.kotlinspirit

import java.lang.IllegalStateException

class OneOrMoreRule<T : Any>(
    private val rule: Rule<T>
) : BaseRule<List<T>>() {
    private val result = ArrayList<T>()

    override fun parse(seek: Int, string: CharSequence): Int {
        var i = seek
        var success = false
        while (i < string.length) {
            val seekBefore = i
            i = rule.parse(seek, string)
            if (i < 0) {
                return if (success) {
                    seekBefore
                } else {
                    i
                }
            } else {
                success = true
            }
        }

        return if (success) {
            i
        } else {
            return -StepCode.EOF
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<List<T>>) {
        var i = seek
        val list = ArrayList<T>()
        val itemResult = ParseResult<T>()
        while (i < string.length) {
            val seekBefore = i
            rule.parseWithResult(seek, string, itemResult)
            i = itemResult.errorCodeOrSeek
            if (i < 0) {
                if (list.isNotEmpty()) {
                    result.data = list
                    result.errorCodeOrSeek = seekBefore
                } else {
                    result.errorCodeOrSeek = i
                }
                return
            } else {
                list.add(itemResult.data ?: throw IllegalStateException("data should not be empty"))
            }
        }

        if (list.isNotEmpty()) {
            result.data = list
            result.errorCodeOrSeek = i
        } else {
            result.errorCodeOrSeek = -StepCode.EOF
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
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
                return if (result.isNotEmpty()) {
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

    override fun clone(): OneOrMoreRule<T> {
        return OneOrMoreRule(
            rule = rule.clone()
        )
    }
}