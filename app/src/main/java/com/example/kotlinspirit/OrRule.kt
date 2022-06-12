package com.example.kotlinspirit

import java.lang.UnsupportedOperationException
import kotlin.math.min

open class OrRule<T : Any>(
    private val a: Rule<T>,
    private val b: Rule<T>
) : Rule<T> {
    private var activeRule = a
    private var stepBeginSeek = -1

    override fun parse(seek: Int, string: CharSequence): Int {
        val aResult = a.parse(seek, string)
        return if (aResult < 0) {
            b.parse(seek, string)
        } else {
            aResult
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        a.parseWithResult(seek, string, result)
        if (result.errorCodeOrSeek < 0) {
            b.parseWithResult(seek, string, result)
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return a.hasMatch(seek, string) || b.hasMatch(seek, string)
    }

    override fun resetStep() {
        activeRule = a
        a.resetStep()
        b.resetStep()
        stepBeginSeek = -1
    }

    override fun getStepParserResult(string: CharSequence): T {
        return activeRule.getStepParserResult(string)
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        if (stepBeginSeek < 0) {
            stepBeginSeek = seek
        }

        val result = activeRule.parseStep(seek, string)
        return if (result.getStepCode().isError()) {
            if (activeRule == b) {
                result
            } else {
                activeRule = b
                createStepResult(
                    seek = stepBeginSeek,
                    stepCode = StepCode.HAS_NEXT
                )
            }
        } else {
            result
        }
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        val aResult = a.noParse(seek, string)
        if (aResult < 0) {
            return aResult
        }

        val bResult = b.noParse(seek, string)
        if (bResult < 0) {
            return bResult
        }

        return min(aResult, bResult)
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        throw UnsupportedOperationException()
    }

    override fun clone(): Rule<T> {
        return OrRule(
            a = a.clone(),
            b = b.clone()
        )
    }
}

class AnyOrRule(a: Rule<Any>, b: Rule<Any>) : OrRule<Any>(a, b) {}