package com.kotlinspirit.expressive

import com.kotlinspirit.core.*
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class OptionalRule<T : Any>(
    private val rule: Rule<T>,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {

    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        val res = rule.parse(seek, string)
        if (res.isComplete) {
            return res
        }

        return ParseSeekResult(seek)
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<T>
    ) {
        rule.parseWithResult(seek, string, result)
        if (result.isError) {
            result.data = null
        }
        result.parseResult = ParseSeekResult(
            seek = result.endSeek,
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        val res = rule.reverseParse(seek, string)
        if (res.isComplete) {
            return res
        }

        return ParseSeekResult(seek)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.reverseParseWithResult(seek, string, result)
        if (result.isError) {
            result.data = null
        }
        result.parseResult = ParseSeekResult(
            seek = result.endSeek,
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun clone(): OptionalRule<T> {
        return OptionalRule(rule.clone(), name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "-${rule.wrappedName}"

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = OptionalRule(
                rule = rule.debug(engine),
                name
            ),
            engine = engine
        )
    }

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun name(name: String): OptionalRule<T> {
        return OptionalRule(rule, name)
    }
}
