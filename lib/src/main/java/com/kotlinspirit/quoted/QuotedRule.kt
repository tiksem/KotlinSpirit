package com.kotlinspirit.quoted

import com.kotlinspirit.core.*
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isError
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class QuotedRule<T : Any>(
    private val main: Rule<T>,
    private val left: Rule<*>,
    private val right: Rule<*>
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
        val r = right.parse(result.seek, string)
        if (r.getParseCode().isError()) {
            result.parseResult = r
            result.data = null
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
        get() = TODO("Not yet implemented")

    override fun isThreadSafe(): Boolean {
        TODO("Not yet implemented")
    }

    override fun clone(): RuleWithDefaultRepeat<T> {
        TODO("Not yet implemented")
    }

    override fun debug(name: String?): RuleWithDefaultRepeat<T> {
        TODO("Not yet implemented")
    }
}