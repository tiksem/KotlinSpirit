package com.kotlinspirit.expressive

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isError
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.repeat.ZeroOrMoreRule
import kotlin.math.min

open class OrRule<T : Any>(
    protected val a: Rule<T>,
    protected val b: Rule<T>
) : RuleWithDefaultRepeat<T>() {
    private var activeRule = a
    private var stepBeginSeek = -1

    override fun parse(seek: Int, string: CharSequence): Long {
        val aResult = a.parse(seek, string)
        return if (aResult.getParseCode().isError()) {
            b.parse(seek, string)
        } else {
            aResult
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        a.parseWithResult(seek, string, result)
        if (result.parseResult.getParseCode().isError()) {
            b.parseWithResult(seek, string, result)
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return a.hasMatch(seek, string) || b.hasMatch(seek, string)
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

    override fun repeat(): Rule<List<T>> {
        return ZeroOrMoreRule(this)
    }

    override fun clone(): OrRule<T> {
        return OrRule(a.clone(), b.clone())
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override fun debug(name: String?): OrRule<T> {
        val a = a.internalDebug()
        val b = b.internalDebug()
        return DebugOrRule(
            name = name ?: "${a.debugNameWrapIfNeed} or ${b.debugNameWrapIfNeed}",
            a, b
        )
    }
}

private class DebugOrRule<T : Any>(
    override val name: String,
    a: Rule<T>,
    b: Rule<T>
) : OrRule<T>(a, b), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<T>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }

    override fun clone(): OrRule<T> {
        return DebugOrRule(name, a.clone(), b.clone())
    }
}

open class AnyOrRule(a: Rule<Any>, b: Rule<Any>) : OrRule<Any>(a, b) {
    override fun debug(name: String?): OrRule<Any> {
        val a = a.internalDebug()
        val b = b.internalDebug()
        return DebugAnyOrRule(
            name = name ?: "${a.debugNameWrapIfNeed} or ${b.debugNameWrapIfNeed}",
            a, b
        )
    }

    override fun clone(): AnyOrRule {
        return AnyOrRule(a.clone(), b.clone())
    }
}

private class DebugAnyOrRule(
    override val name: String,
    a: Rule<Any>,
    b: Rule<Any>
) : AnyOrRule(a, b), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<Any>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }

    override fun clone(): AnyOrRule {
        return DebugAnyOrRule(name, a.clone(), b.clone())
    }
}