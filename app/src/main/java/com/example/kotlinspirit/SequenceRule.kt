package com.example.kotlinspirit

import java.lang.UnsupportedOperationException

class SequenceRule(
    private val a: Rule<*>,
    private val b: Rule<*>
) : Rule<CharSequence> {
    private var stepBeginSeek = -1
    private var stepEndSeek = -1
    private var activeRule = a

    init {
        
    }

    override fun parse(seek: Int, string: CharSequence): Int {
        val aResult = a.parse(seek, string)
        if (aResult < 0) {
            return aResult
        }

        return b.parse(aResult, string)
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<CharSequence>
    ) {
        val seekOrError = parse(seek, string)
        result.errorCodeOrSeek = seekOrError
        if (seekOrError >= 0) {
            result.data = string.subSequence(seek, seekOrError)
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val aResult = a.parse(seek, string)
        if (aResult < 0) {
            return false
        } else {
            return b.hasMatch(aResult, string)
        }
    }

    override fun resetStep() {
        a.resetStep()
        b.resetStep()
        activeRule = a
        stepBeginSeek = -1
    }

    override fun getStepParserResult(string: CharSequence): CharSequence {
        return string.subSequence(stepBeginSeek, stepEndSeek)
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        if (stepBeginSeek < 0) {
            stepBeginSeek = seek
        }

        val result = activeRule.parseStep(seek, string)
        return if (result.getStepCode() == StepCode.COMPLETE) {
            if (activeRule == a) {
                activeRule = b
                createStepResult(
                    seek = result.getSeek(),
                    stepCode = StepCode.HAS_NEXT
                )
            } else {
                stepEndSeek = result.getSeek()
                result
            }
        } else {
            result
        }
    }

    override fun resetNoStep() {
        a.resetNoStep()
        b.resetNoStep()
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        val aNoParseResult = a.noParse(seek, string)
        return if (aNoParseResult < 0) {
            val aSeek = a.parse(seek, string)
            b.noParse(aSeek, string)
        } else {
            aNoParseResult
        }
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        throw UnsupportedOperationException()
    }

    override fun clone(): Rule<CharSequence> {
        return SequenceRule(a.clone(), b.clone())
    }
}