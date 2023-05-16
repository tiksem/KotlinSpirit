package com.kotlinspirit.quoted

import com.kotlinspirit.core.*
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isError
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class QuotedRule<T : Any>(
    private val main: Rule<T>,
    private val left: Rule<*>,
    private val right: Rule<*>,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {
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
        return QuotedRule(main.clone(), left.clone(), right.clone(), name)
    }

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = QuotedRule(main.debug(engine), left.debug(engine), right.debug(engine), name),
            engine = engine
        )
    }

    override fun name(name: String): QuotedRule<T> {
        return QuotedRule(main, left, right, name)
    }

    override val defaultDebugName: String
        get() = "${main.wrappedName}.quoted(${left.debugName}, ${right.debugName})"

    override fun getPrefixMaxLength(): Int {
        return (
                left.getPrefixMaxLength().toLong() +
                        right.getPrefixMaxLength() +
                        main.getPrefixMaxLength()
                ).coerceAtMost(MAX_PREFIX_LENGTH.toLong()).toInt()
    }

    override fun isPrefixFixedLength(): Boolean {
        return left.isPrefixFixedLength() && right.isPrefixFixedLength() && main.isPrefixFixedLength()
    }
}