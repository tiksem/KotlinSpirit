package com.kotlinspirit.expressive

import com.kotlinspirit.core.*
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class FailIfRule<T : Any>(
    private val rule: Rule<T>,
    private val failPredicate: (T) -> Boolean,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        val result = ParseResult<T>()
        rule.parseWithResult(seek, string, result)
        if (result.isError) {
            return result.parseResult
        }

        val data = result.data ?: throw IllegalStateException("rule produces null, without error")
        if (failPredicate(data)) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.FAIL_PREDICATE
            )
        }

        return result.parseResult
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (result.isError) {
            return
        }

        val data = result.data ?: throw IllegalStateException("rule produces null, without error")
        if (failPredicate(data)) {
             result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.FAIL_PREDICATE
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode().isNotError()
    }

    override fun failIf(predicate: (T) -> Boolean): FailIfRule<T> {
        return FailIfRule(rule = rule, failPredicate = {
            failPredicate(it) || predicate(it)
        })
    }

    override fun clone(): RuleWithDefaultRepeat<T> {
        return FailIfRule(rule.clone(), failPredicate, name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "failIf"

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = FailIfRule(rule.debug(engine), failPredicate, name),
            engine = engine
        )
    }

    override fun name(name: String): Rule<T> {
        return FailIfRule(rule, failPredicate, name)
    }

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun ignoreCallbacks(): FailIfRule<T> {
        return FailIfRule(rule.ignoreCallbacks(), failPredicate)
    }
}