package com.example.kotlinspirit

import java.lang.UnsupportedOperationException

class StringOneOrMoreCharPredicateRule(
    private val predicate: (Char) -> Boolean
) : RuleWithDefaultRepeat<CharSequence>() {
    private var stepSeekBegin = -1
    private var result: CharSequence = ""

    override fun parse(seek: Int, string: CharSequence): Long {
        var i = seek
        while (i < string.length) {
            val c = string[i]
            if (!predicate(c)) {
                return if (i - seek >= 1) {
                    createComplete(i)
                } else {
                    createStepResult(
                        seek = i,
                        StepCode.STRING_NOT_ENOUGH_DATA
                    )
                }
            }

            i++
        }

        return if (i - seek >= 1) {
            createComplete(i)
        } else {
            createStepResult(
                seek = i,
                StepCode.STRING_NOT_ENOUGH_DATA
            )
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        var i = seek
        while (i < string.length) {
            val c = string[i]
            if (!predicate(c)) {
                if (i - seek >= 1) {
                    result.data = string.subSequence(seek, i)
                    result.stepResult = createComplete(i)
                } else {
                    result.stepResult = createStepResult(
                        seek = i,
                        StepCode.STRING_NOT_ENOUGH_DATA
                    )
                }
                return
            }

            i++
        }

        if (i - seek >= 1) {
            result.data = string.subSequence(seek, i)
            result.stepResult = createComplete(i)
        } else {
            result.stepResult = createStepResult(
                seek = i,
                StepCode.STRING_NOT_ENOUGH_DATA
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < string.length && predicate(string[seek])
    }

    override fun resetStep() {
        stepSeekBegin = -1
        result = ""
    }

    override fun getStepParserResult(string: CharSequence): CharSequence {
        return result
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        if (stepSeekBegin < 0) {
            stepSeekBegin = seek
        }

        if (seek >= string.length) {
            return if (seek > stepSeekBegin) {
                result = string.subSequence(stepSeekBegin, seek)
                notifyParseStepComplete(string)
                createStepResult(
                    seek = seek,
                    stepCode = StepCode.COMPLETE
                )
            } else {
                createStepResult(
                    seek = seek,
                    stepCode = StepCode.STRING_NOT_ENOUGH_DATA
                )
            }
        }

        val char = string[seek]
        return if (predicate(char)) {
            createStepResult(
                seek = seek + 1,
                stepCode = StepCode.MAY_COMPLETE
            )
        } else {
            if (seek > stepSeekBegin) {
                result = string.subSequence(stepSeekBegin, seek)
                notifyParseStepComplete(string)
                createStepResult(
                    seek = seek,
                    stepCode = StepCode.COMPLETE
                )
            } else {
                createStepResult(
                    seek = seek,
                    stepCode = StepCode.STRING_NOT_ENOUGH_DATA
                )
            }
        }
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        throw UnsupportedOperationException()
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        throw UnsupportedOperationException()
    }

    override fun not(): StringOneOrMoreCharPredicateRule {
        return StringOneOrMoreCharPredicateRule(
            predicate = {
                !predicate(it)
            }
        )
    }

    override fun clone(): StringOneOrMoreCharPredicateRule {
        return StringOneOrMoreCharPredicateRule(predicate)
    }
}