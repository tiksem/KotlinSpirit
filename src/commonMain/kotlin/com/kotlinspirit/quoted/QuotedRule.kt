package com.kotlinspirit.quoted

import com.kotlinspirit.core.*
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class QuotedRule<T : Any>(
    private val main: Rule<T>,
    private val left: Rule<*>,
    private val right: Rule<*>,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        val l = left.parse(seek, string)
        if (l.isError) {
            return l
        }
        val m = main.parse(l.seek, string)
        if (m.isError) {
            return m
        }
        return right.parse(m.seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val l = left.parse(seek, string)
        if (l.isError) {
            result.parseResult = l
            result.data = null
            return
        }
        main.parseWithResult(l.seek, string, result)
        if (result.isError) {
            return
        }
        val r = right.parse(result.endSeek, string)
        result.parseResult = r
        if (r.isError) {
            result.data = null
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val l = left.parse(seek, string)
        if (l.isError) {
            return false
        }
        val m = main.parse(l.seek, string)
        if (m.isError) {
            return false
        }
        return right.hasMatch(m.seek, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        val r = right.reverseParse(seek, string)
        if (r.isError) {
            return r
        }
        val m = main.reverseParse(r.seek, string)
        if (m.isError) {
            return m
        }
        return left.reverseParse(m.seek, string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val r = right.reverseParse(seek, string)
        if (r.isError) {
            result.parseResult = r
            result.data = null
            return
        }
        main.reverseParseWithResult(r.seek, string, result)
        if (result.isError) {
            return
        }
        val l = left.reverseParse(result.endSeek, string)
        result.parseResult = l
        if (r.isError) {
            result.data = null
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        val r = right.reverseParse(seek, string)
        if (r.isError) {
            return false
        }
        val m = main.reverseParse(r.seek, string)
        if (m.isError) {
            return false
        }
        return left.reverseHasMatch(m.seek, string)
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
}