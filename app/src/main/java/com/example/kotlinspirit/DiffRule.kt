package com.example.kotlinspirit

import java.lang.UnsupportedOperationException

class DiffRule<T : Any>(
    private val main: Rule<T>,
    private val diff: Rule<*>
) : RuleWithDefaultRepeat<T>() {
    private var mainMayCompleteSeek: Int = -1

    override fun resetStep() {
        main.resetStep()
        diff.resetStep()
        mainMayCompleteSeek = -1
    }

    override fun getStepParserResult(string: CharSequence): T {
        return main.getStepParserResult(string)
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        return if (diff.hasMatch(seek, string)) {
            if (mainMayCompleteSeek < 0) {
                createStepResult(
                    seek, StepCode.DIFF_FAILED
                )
            } else {
                main.notifyParseStepComplete(string)
                createStepResult(
                    seek = mainMayCompleteSeek,
                    stepCode = StepCode.COMPLETE
                )
            }
        } else {
            main.parseStep(seek, string).also {
                if (it.getStepCode() == StepCode.MAY_COMPLETE) {
                    mainMayCompleteSeek = it.getSeek()
                }
            }
        }
    }

    override fun clone(): DiffRule<T> {
        return DiffRule(
            main = main.clone(),
            diff = diff.clone()
        )
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        throw UnsupportedOperationException()
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        throw UnsupportedOperationException()
    }

    override fun not(): Rule<*> {
        return DiffRule(
            main = diff.clone(),
            diff = main.clone()
        )
    }
}