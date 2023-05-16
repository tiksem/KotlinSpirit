package com.kotlinspirit.expressive

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class SuffixExpectationRule<T : Any>(
    private val a: Rule<T>,
    private val b: Rule<*>,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        val aResult = a.parse(seek, string)
        if (aResult.getParseCode().isError()) {
            return aResult
        }

        val bResult = b.parse(aResult.getSeek(), string)
        val parseCode = bResult.getParseCode()
        return if (parseCode.isError()) {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.SUFFIX_EXPECTATION_FAILED
            )
        } else {
            aResult
        }
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<T>
    ) {
        a.parseWithResult(seek, string, result)
        if (result.isError) {
            return
        }

        val bResult = b.parse(result.endSeek, string)
        if (bResult.getParseCode().isError()) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.SUFFIX_EXPECTATION_FAILED
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val aResult = a.parse(seek, string)
        if (aResult.getParseCode().isError()) {
            return false
        }

        return b.hasMatch(aResult.getSeek(), string)
    }

    override fun clone(): SuffixExpectationRule<T> {
        return SuffixExpectationRule(a.clone(), b.clone(), name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override val defaultDebugName: String
        get() = "${a.wrappedName} expects ${b.wrappedName}"

    override fun isThreadSafe(): Boolean {
        return a.isThreadSafe() && b.isThreadSafe()
    }

    override fun name(name: String): SuffixExpectationRule<T> {
        return SuffixExpectationRule(a, b, name)
    }

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = SuffixExpectationRule(a.debug(engine), b.debug(engine), name),
            engine = engine
        )
    }

    override fun ignoreCallbacks(): SuffixExpectationRule<T> {
        return SuffixExpectationRule(a.ignoreCallbacks(), b.ignoreCallbacks())
    }

    override fun getPrefixMaxLength(): Int {
        return a.getPrefixMaxLength()
    }

    override fun isPrefixFixedLength(): Boolean {
        return a.isPrefixFixedLength()
    }
}