package com.kotlinspirit.expressive

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isError
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RepeatRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.str.ExactStringRule

open class OrRule<T : Any>(
    protected val a: Rule<T>,
    protected val b: Rule<T>,
    name: String? = null,
) : RuleWithDefaultRepeat<T>(name) {
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

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        val aResult = a.reverseParse(seek, string)
        return if (aResult.getParseCode().isError()) {
            b.reverseParse(seek, string)
        } else {
            aResult
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        a.reverseParseWithResult(seek, string, result)
        if (result.parseResult.getParseCode().isError()) {
            b.reverseParseWithResult(seek, string, result)
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return a.reverseHasMatch(seek, string) || b.reverseHasMatch(seek, string)
    }

    override fun repeat(): Rule<List<T>> {
        return RepeatRule(this, range = 0..Int.MAX_VALUE)
    }

    override fun clone(): OrRule<T> {
        return OrRule(a.clone(), b.clone(), name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override val defaultDebugName: String
        get() = "${a.wrappedName} or ${b.wrappedName}"

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = OrRule(
                a.debug(engine),
                b.debug(engine),
                name
            ),
            engine
        )
    }

    override fun name(name: String): OrRule<T> {
        return OrRule(a, b, name)
    }

    override fun isThreadSafe(): Boolean {
        return a.isThreadSafe() && b.isThreadSafe()
    }
}

class AnyOrRule(a: Rule<Any>, b: Rule<Any>, name: String? = null) : OrRule<Any>(a, b, name) {
    override fun clone(): AnyOrRule {
        return AnyOrRule(a.clone(), b.clone(), name)
    }
}

class StringOrRule(a: Rule<CharSequence>, b: Rule<CharSequence>, name: String? = null) : OrRule<CharSequence>(a, b, name) {
    override fun clone(): StringOrRule {
        return StringOrRule(a.clone(), b.clone(), name)
    }

    override fun or(string: String): StringOrRule {
        return this or ExactStringRule(string)
    }

    infix fun or(a: ExactStringRule): StringOrRule {
        return StringOrRule(this, a)
    }
}