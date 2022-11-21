package com.kotlinspirit.core

import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.ext.*
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult

internal abstract class BaseParser<T : Any> : Parser<T> {
    protected abstract fun getRule(): Rule<T>

    override fun parseGetResultOrThrow(string: CharSequence): T {
        val result = ParseResult<T>()
        val rule = getRule()
        rule.parseWithResult(0, string, result)
        val stepResult = result.parseResult
        if (stepResult.getParseCode().isError()) {
            throw ParseException(
                stepResult, string,
                if (rule is DebugRule) DebugEngine.root else null
            )
        } else {
            return result.data!!
        }
    }

    override fun parseOrThrow(string: CharSequence): Int {
        val rule = getRule()
        val result = rule.parse(0, string)
        if (result.getParseCode().isError()) {
            throw ParseException(
                result, string,
                if (rule is DebugRule) DebugEngine.root else null
            )
        }

        return result.getSeek()
    }

    override fun tryParse(string: CharSequence): Int? {
        val rule = getRule()
        val result = rule.parse(0, string)
        if (result.getParseCode().isError()) {
            return null
        }

        return result.getSeek()
    }

    override fun parseWithResult(string: CharSequence): ParseResult<T> {
        val result = ParseResult<T>()
        val rule = getRule()
        rule.parseWithResult(0, string, result)
        return result
    }

    override fun parse(string: CharSequence): ParseSeekResult {
        val rule = getRule()
        val result = rule.parse(0, string)
        return ParseSeekResult(stepResult = result)
    }

    override fun matchOrThrow(string: CharSequence) {
        val rule = getRule()
        val result = rule.parse(0, string)
        if (result.getParseCode().isError()) {
            throw ParseException(
                result, string,
                if (rule is DebugRule) DebugEngine.root else null
            )
        }

        val seek = result.getSeek()
        if (seek != string.length) {
            throw ParseException(
                result = createStepResult(
                    seek = seek,
                    parseCode = ParseCode.WHOLE_STRING_DOES_NOT_MATCH
                ),
                string = string,
                debugTree = if (rule is DebugRule) DebugEngine.root else null
            )
        }
    }

    override fun matches(string: CharSequence): Boolean {
        return string.matches(getRule())
    }

    override fun matchesAtBeginning(string: CharSequence): Boolean {
        return getRule().hasMatch(0, string)
    }

    override fun replaceFirst(source: CharSequence, replacement: CharSequence): CharSequence {
        return source.replaceFirst(getRule(), replacement)
    }

    override fun replaceAll(source: CharSequence, replacement: CharSequence): CharSequence {
        return source.replaceAll(getRule(), replacement)
    }

    override fun replaceFirst(source: CharSequence, replacementProvider: (T) -> Any): CharSequence {
        return source.replaceFirst(getRule(), replacementProvider)
    }

    override fun replaceAll(source: CharSequence, replacementProvider: (T) -> Any): CharSequence {
        return source.replaceAll(getRule(), replacementProvider)
    }

    override fun replaceFirstOrNull(source: CharSequence, replacement: CharSequence): CharSequence? {
        return source.replaceFirstOrNull(getRule(), replacement)
    }

    override fun replaceFirstOrNull(source: CharSequence, replacementProvider: (T) -> CharSequence): CharSequence? {
        return source.replaceFirstOrNull(getRule(), replacementProvider)
    }

    override fun indexOf(string: CharSequence): Int? {
        return string.indexOf(getRule())
    }

    override fun findFirstResult(string: CharSequence): ParseRangeResult<T>? {
        return string.findFirstResult(getRule())
    }

    override fun findFirstRange(string: CharSequence): ParseRange? {
        return string.findFirstRange(getRule())
    }

    override fun findFirst(string: CharSequence): T? {
        return string.findFirst(getRule())
    }

    override fun findAll(string: CharSequence): List<T> {
        return string.findAll(getRule())
    }

    override fun findAllResults(string: CharSequence): List<ParseRangeResult<T>> {
        return string.findAllResults(getRule())
    }
}
