package com.example.kotlinspirit

import java.lang.UnsupportedOperationException

class SequenceRule(
    private val a: Rule<*>,
    private val b: Rule<*>
) : BaseRule<CharSequence>() {
    private var stepBeginSeek = -1
    private var stepEndSeek = -1
    private var activeRule = a

    init {

    }

    override fun parse(seek: Int, string: CharSequence): Long {
        val aResult = a.parse(seek, string)
        if (aResult.getStepCode().isError()) {
            return aResult
        }

        return b.parse(aResult.getSeek(), string)
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<CharSequence>
    ) {
        val parseResult = parse(seek, string)
        result.stepResult = parseResult
        if (parseResult.getStepCode().isNotError()) {
            result.data = string.subSequence(seek, parseResult.getSeek())
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val aResult = a.parse(seek, string)
        return if (aResult.getStepCode().isError()) {
            false
        } else {
            b.hasMatch(aResult.getSeek(), string)
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
            val aParseResult = a.parse(seek, string)
            b.noParse(aParseResult.getSeek(), string)
        } else {
            aNoParseResult
        }
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        throw UnsupportedOperationException()
    }

    override fun clone(): SequenceRule {
        return SequenceRule(a.clone(), b.clone())
    }
}