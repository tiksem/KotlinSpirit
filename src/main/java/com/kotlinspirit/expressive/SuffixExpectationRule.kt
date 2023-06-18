package com.kotlinspirit.expressive

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class SuffixExpectationRule<T : Any>(
    private val body: Rule<T>,
    private val suffix: Rule<*>,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        val bodyResult = body.parse(seek, string)
        if (bodyResult.getParseCode().isError()) {
            return bodyResult
        }

        val suffixResult = suffix.parse(bodyResult.getSeek(), string)
        val parseCode = suffixResult.getParseCode()
        return if (parseCode.isError()) {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.SUFFIX_EXPECTATION_FAILED
            )
        } else {
            bodyResult
        }
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<T>
    ) {
        body.parseWithResult(seek, string, result)
        if (result.isError) {
            return
        }

        val suffixResult = suffix.parse(result.endSeek, string)
        if (suffixResult.getParseCode().isError()) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.SUFFIX_EXPECTATION_FAILED
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val bodyResult = body.parse(seek, string)
        if (bodyResult.getParseCode().isError()) {
            return false
        }

        return suffix.hasMatch(bodyResult.getSeek(), string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        val bodyResult = body.reverseParse(seek, string)
        if (bodyResult.getParseCode().isError()) {
            return bodyResult
        }

        val suffixResult = suffix.parse(seek + 1, string)
        val parseCode = suffixResult.getParseCode()
        return if (parseCode.isError()) {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.SUFFIX_EXPECTATION_FAILED
            )
        } else {
            bodyResult
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        body.reverseParseWithResult(seek, string, result)
        if (result.isError) {
            return
        }

        val suffixResult = suffix.parse(seek + 1, string)
        if (suffixResult.getParseCode().isError()) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.SUFFIX_EXPECTATION_FAILED
            )
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return body.reverseHasMatch(seek, string) && suffix.hasMatch(seek + 1, string)
    }

    override fun clone(): SuffixExpectationRule<T> {
        return SuffixExpectationRule(body.clone(), suffix.clone(), name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override val defaultDebugName: String
        get() = "${body.wrappedName} expects ${suffix.wrappedName}"

    override fun isThreadSafe(): Boolean {
        return body.isThreadSafe() && suffix.isThreadSafe()
    }

    override fun name(name: String): SuffixExpectationRule<T> {
        return SuffixExpectationRule(body, suffix, name)
    }

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = SuffixExpectationRule(body.debug(engine), suffix.debug(engine), name),
            engine = engine
        )
    }

    override fun ignoreCallbacks(): SuffixExpectationRule<T> {
        return SuffixExpectationRule(body.ignoreCallbacks(), suffix.ignoreCallbacks())
    }
}