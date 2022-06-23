package com.example.kotlinspirit

import java.lang.UnsupportedOperationException
import kotlin.math.min

open class OrRule<T : Any>(
    private val a: Rule<T>,
    private val b: Rule<T>
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
        if (result.stepResult.getParseCode().isError()) {
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

    override fun clone(): OrRule<T> {
        return OrRule(
            a = a.clone(),
            b = b.clone()
        )
    }

    override fun repeat(): Rule<List<T>> {
        return ZeroOrMoreRule(this)
    }
}

class AnyOrRule(a: Rule<Any>, b: Rule<Any>) : OrRule<Any>(a, b) {}