package com.kotlinspirit.debug

import com.kotlinspirit.core.BaseRuleWithResult
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.hook.ParseRange
import com.kotlinspirit.hook.ParseRangeResult

internal class DebugRule<T : Any>(
    private val rule: Rule<T>,
    private val engine: DebugEngine
) : Rule<T>(rule.debugName) {
    override fun parse(seek: Int, string: CharSequence): Long {
        engine.ruleParseStarted(rule = this, startSeek = seek)
        return rule.parse(seek, string).also {
            engine.ruleParseEnded(rule = this, result = it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        engine.ruleParseStarted(rule = this, startSeek = seek)
        rule.parseWithResult(seek, string, result)
        engine.ruleParseEnded(rule = this, result = result.parseResult, data = result.data)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
    }

    override fun repeat(): Rule<*> {
        throw UnsupportedOperationException("Method is not supported by DebugRule")
    }

    override fun repeat(range: IntRange): Rule<*> {
        throw UnsupportedOperationException("Method is not supported by DebugRule")
    }

    override fun unaryPlus(): Rule<*> {
        throw UnsupportedOperationException("Method is not supported by DebugRule")
    }

    override fun invoke(callback: (T) -> Unit): BaseRuleWithResult<T> {
        throw UnsupportedOperationException("Method is not supported by DebugRule")
    }

    override fun getRange(out: ParseRange): Rule<T> {
        throw UnsupportedOperationException("Method is not supported by DebugRule")
    }

    override fun getRange(callback: (ParseRange) -> Unit): Rule<T> {
        throw UnsupportedOperationException("Method is not supported by DebugRule")
    }

    override fun getRangeResult(out: ParseRangeResult<T>): Rule<T> {
        throw UnsupportedOperationException("Method is not supported by DebugRule")
    }

    override fun getRangeResult(callback: (ParseRangeResult<T>) -> Unit): Rule<T> {
        throw UnsupportedOperationException("Method is not supported by DebugRule")
    }

    override fun ignoreCallbacks(): Rule<T> {
        throw UnsupportedOperationException("Method is not supported by DebugRule")
    }

    override fun clone(): Rule<T> {
        throw UnsupportedOperationException("Method is not supported by DebugRule")
    }

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun name(name: String): Rule<T> {
        throw UnsupportedOperationException("Method is not supported by DebugRule")
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = rule.debugNameShouldBeWrapped

    override val defaultDebugName: String
        get() = rule.defaultDebugName
}