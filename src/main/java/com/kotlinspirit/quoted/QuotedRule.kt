package com.kotlinspirit.quoted

import com.kotlinspirit.core.*
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isError
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

open class QuotedRule<T : Any>(
    protected val main: Rule<T>,
    protected val left: Rule<*>,
    protected val right: Rule<*>
) : RuleWithDefaultRepeat<T>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        val l = left.parse(seek, string)
        if (l.getParseCode().isError()) {
            return l
        }
        val m = main.parse(l.getSeek(), string)
        if (m.getParseCode().isError()) {
            return m
        }
        return right.parse(m.getSeek(), string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val l = left.parse(seek, string)
        if (l.getParseCode().isError()) {
            result.parseResult = l
            return
        }
        main.parseWithResult(l.getSeek(), string, result)
        if (result.isError) {
            return
        }
        val r = right.parse(result.endSeek, string)
        result.parseResult = r
        if (r.getParseCode().isError()) {
            result.data = null
        } else {
            result.parseResult
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val l = left.parse(seek, string)
        if (l.getParseCode().isError()) {
            return false
        }
        val m = main.parse(l.getSeek(), string)
        if (m.getParseCode().isError()) {
            return false
        }
        return right.hasMatch(m.getSeek(), string)
    }

    override fun ignoreCallbacks(): QuotedRule<T> {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override fun isThreadSafe(): Boolean {
        return main.isThreadSafe() && left.isThreadSafe() && right.isThreadSafe()
    }

    override fun clone(): QuotedRule<T> {
        return QuotedRule(main.clone(), left.clone(), right.clone())
    }

    override fun debug(name: String?): QuotedRule<T> {
        val main = main.internalDebug()
        val left = left.internalDebug()
        val right = right.internalDebug()

        val n = name ?: arrayOf(
            left.debugNameWrapIfNeed,
            main.debugNameWrapIfNeed,
            right.debugNameWrapIfNeed
        ).joinToString(" + ")
        return DebugQuotedRule(n, main, left, right)
    }
}

private class DebugQuotedRule<T : Any>(
    override val name: String,
    main: Rule<T>,
    left: Rule<*>,
    right: Rule<*>
): QuotedRule<T>(main, left, right), DebugRule {
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

    override fun clone(): QuotedRule<T> {
        return DebugQuotedRule(name, main.clone(), left.clone(), right.clone())
    }
}