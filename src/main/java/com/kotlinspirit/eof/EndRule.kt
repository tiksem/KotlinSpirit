package com.kotlinspirit.eof

import com.kotlinspirit.core.*
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult

class EndRule(name: String? = null) : RuleWithDefaultSequenceBehavior<Unit>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        if (seek == string.length) {
            return ParseSeekResult(seek)
        }

        return ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.NO_EOF
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Unit>) {
        val parseResult = parse(seek, string)
        result.parseResult = parseResult
        if (parseResult.isComplete) {
            result.data = Unit
        } else {
            result.data = null
        }
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        if (seek < 0) {
            return ParseSeekResult(seek)
        }

        return ParseSeekResult(
            seek = seek,
            parseCode = ParseCode.NO_EOF
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<Unit>) {
        val parseResult = parse(seek, string)
        result.parseResult = parseResult
        if (parseResult.isComplete) {
            result.data = Unit
        } else {
            result.data = null
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < 0
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek == string.length
    }

    override fun repeat(): Rule<*> {
        throw UnsupportedOperationException("repeat is not supported for eof rule")
    }

    override fun repeat(range: IntRange): Rule<*> {
        throw UnsupportedOperationException("repeat is not supported for eof rule")
    }

    override fun repeat(count: Int): Rule<*> {
        throw UnsupportedOperationException("repeat is not supported for eof rule")
    }

    override fun unaryPlus(): Rule<*> {
        throw UnsupportedOperationException("+ is not supported for eof rule")
    }

    override fun invoke(callback: (Unit) -> Unit): BaseRuleWithResult<Unit> {
        throw UnsupportedOperationException("result callback is not supported for eof rule")
    }

    override fun getRange(out: ParseRange): Rule<Unit> {
        throw UnsupportedOperationException("getRange is not supported for eof rule")
    }

    override fun getRange(callback: (ParseRange) -> Unit): Rule<Unit> {
        throw UnsupportedOperationException("getRange is not supported for eof rule")
    }

    override fun getRangeResult(out: ParseRangeResult<Unit>): Rule<Unit> {
        throw UnsupportedOperationException("getRangeResult is not supported for eof rule")
    }

    override fun getRangeResult(callback: (ParseRangeResult<Unit>) -> Unit): Rule<Unit> {
        throw UnsupportedOperationException("getRangeResult is not supported for eof rule")
    }

    override fun clone(): EndRule {
        return this
    }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun name(name: String): EndRule {
        return EndRule(name);
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false
    override val defaultDebugName: String
        get() = "eof"
}