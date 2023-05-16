package com.kotlinspirit.expressive

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isError
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.repeat.ZeroOrMoreRule
import kotlin.math.max

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

    override fun repeat(): Rule<List<T>> {
        return ZeroOrMoreRule(this)
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

    override fun ignoreCallbacks(): OrRule<T> {
        return OrRule(a.ignoreCallbacks(), b.ignoreCallbacks(), name)
    }

    override fun getPrefixMaxLength(): Int {
        return max(a.getPrefixMaxLength(), b.getPrefixMaxLength())
    }

    override fun isPrefixFixedLength(): Boolean {
        return a.isPrefixFixedLength() && b.isPrefixFixedLength() && a.getPrefixMaxLength() == b.getPrefixMaxLength()
    }
}

class AnyOrRule(a: Rule<Any>, b: Rule<Any>, name: String? = null) : OrRule<Any>(a, b, name) {
    override fun clone(): AnyOrRule {
        return AnyOrRule(a.clone(), b.clone(), name)
    }
}