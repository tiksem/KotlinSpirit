package com.kotlinspirit.eof

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult

class EofRule(name: String? = null) : Rule<Unit>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        if (seek == string.length) {
            return createComplete(seek)
        }

        return createStepResult(
            seek = seek,
            parseCode = ParseCode.NO_EOF
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Unit>) {
        val parseResult = parse(seek, string)
        result.parseResult = parseResult
        if (parseResult.getParseCode().isNotError()) {
            result.data = Unit
        } else {
            result.data = null
        }
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

    override fun ignoreCallbacks(): EofRule {
        return this
    }

    override fun clone(): EofRule {
        return this
    }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun isDynamic(): Boolean {
        return false
    }

    override fun name(name: String): EofRule {
        return EofRule(name);
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false
    override val defaultDebugName: String
        get() = "eof"
}