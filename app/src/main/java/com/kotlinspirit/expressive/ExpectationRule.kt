package com.kotlinspirit.expressive

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import java.lang.IllegalStateException

open class ExpectationRule<T : Any>(
    protected val a: Rule<T>,
    protected val b: Rule<*>
) : RuleWithDefaultRepeat<T>() {
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
                parseCode = ParseCode.EXPECTATION_FAILED
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

        val bResult = b.parse(result.seek, string)
        if (bResult.getParseCode().isError()) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EXPECTATION_FAILED
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

    override fun noParse(seek: Int, string: CharSequence): Int {
        val aResult = a.noParse(seek, string)
        if (aResult < 0 || aResult >= string.length) {
            return aResult
        }

        val aParseResult = a.parse(aResult, string)
        if (aParseResult.getParseCode().isError()) {
            throw IllegalStateException("Undefined behaviour, internal error")
        }

        val bParse = b.noParse(aParseResult.getSeek(), string)
        return if (bParse > aResult) {
            noParse(bParse, string)
        } else {
            aResult
        }
    }

    override fun clone(): ExpectationRule<T> {
        return ExpectationRule(a.clone(), b.clone())
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override fun debug(name: String?): ExpectationRule<T> {
        val a = a.internalDebug()
        val b = b.internalDebug()
        return DebugExpectationRule(name ?: "${a.debugName} expects ${b.debugName}", a, b)
    }

    override fun isThreadSafe(): Boolean {
        return a.isThreadSafe() && b.isThreadSafe()
    }

    override fun ignoreCallbacks(): ExpectationRule<T> {
        return ExpectationRule(a.ignoreCallbacks(), b.ignoreCallbacks())
    }
}

private class DebugExpectationRule<T : Any>(
    override val name: String,
    a: Rule<T>,
    b: Rule<*>
) : ExpectationRule<T>(a, b), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }

    override fun clone(): ExpectationRule<T> {
        return DebugExpectationRule(name, a.clone(), b.clone())
    }
}