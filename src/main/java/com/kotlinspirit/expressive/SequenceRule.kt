package com.kotlinspirit.expressive

import com.kotlinspirit.core.*
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class SequenceRule(
    private val a: Rule<*>,
    private val b: Rule<*>,
    name: String? = null
) : RuleWithDefaultRepeat<CharSequence>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        val aResult = a.parse(seek, string)
        if (aResult.isError) {
            return aResult
        }

        return b.parse(aResult.seek, string)
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<CharSequence>
    ) {
        val parseResult = parse(seek, string)
        result.parseResult = parseResult
        if (parseResult.isComplete) {
            result.data = string.subSequence(seek, parseResult.seek)
        } else {
            result.data = null
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val aResult = a.parse(seek, string)
        return if (aResult.isError) {
            false
        } else {
            b.hasMatch(aResult.seek, string)
        }
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        val bResult = b.reverseParse(seek, string)
        if (bResult.isError) {
            return bResult
        }

        return a.reverseParse(bResult.seek, string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        val parseResult = reverseParse(seek, string)
        result.parseResult = parseResult
        if (parseResult.isComplete) {
            result.data = string.subSequence(parseResult.seek + 1, seek)
        } else {
            result.data = null
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        val bResult = b.reverseParse(seek, string)
        return if (bResult.isError) {
            false
        } else {
            a.reverseHasMatch(bResult.seek, string)
        }
    }

    override fun clone(): SequenceRule {
        return SequenceRule(a.clone(), b.clone(), name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override val defaultDebugName: String
        get() = "${a.wrappedName}+${b.wrappedName}"

    override fun debug(engine: DebugEngine): DebugRule<CharSequence> {
        return DebugRule(
            rule = SequenceRule(a.debug(engine), b.debug(engine), name),
            engine = engine
        )
    }

    override fun name(name: String): SequenceRule {
        return SequenceRule(a, b, name)
    }

    override fun isThreadSafe(): Boolean {
        return a.isThreadSafe() && b.isThreadSafe()
    }

    override fun repeat(): Rule<List<CharSequence>> {
        return super.repeat()
    }
}