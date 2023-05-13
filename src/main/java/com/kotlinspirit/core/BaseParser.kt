package com.kotlinspirit.core

import com.kotlinspirit.ext.*
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult

internal abstract class BaseParser<T : Any> : Parser<T> {
    protected abstract fun getRule(string: CharSequence): Rule<T>

    override fun parseGetResultOrThrow(string: CharSequence): T {
        val result = ParseResult<T>()
        val rule = getRule(string)
        rule.parseWithResult(0, string, result)
        val stepResult = result.parseResult
        if (stepResult.getParseCode().isError()) {
            throw ParseException(stepResult, string)
        } else {
            return result.data!!
        }
    }

    override fun parseOrThrow(string: CharSequence): Int {
        val rule = getRule(string)
        val result = rule.parse(0, string)
        if (result.getParseCode().isError()) {
            throw ParseException(result, string)
        }

        return result.getSeek()
    }

    override fun tryParse(string: CharSequence): Int? {
        val rule = getRule(string)
        val result = rule.parse(0, string)
        if (result.getParseCode().isError()) {
            return null
        }

        return result.getSeek()
    }

    override fun parseWithResult(string: CharSequence): ParseResult<T> {
        val result = ParseResult<T>()
        val rule = getRule(string)
        rule.parseWithResult(0, string, result)
        return result
    }

    override fun parse(string: CharSequence): ParseSeekResult {
        val rule = getRule(string)
        val result = rule.parse(0, string)
        return ParseSeekResult(stepResult = result)
    }

    override fun matchOrThrow(string: CharSequence) {
        val rule = getRule(string)
        val result = rule.parse(0, string)
        if (result.getParseCode().isError()) {
            throw ParseException(result, string)
        }

        val seek = result.getSeek()
        if (seek != string.length) {
            throw ParseException(
                result = createStepResult(
                    seek = seek,
                    parseCode = ParseCode.WHOLE_STRING_DOES_NOT_MATCH
                ),
                string = string
            )
        }
    }

    override fun matches(string: CharSequence): Boolean {
        return string.matches(getRule(string))
    }

    override fun matchesAtBeginning(string: CharSequence): Boolean {
        return getRule(string).hasMatch(0, string)
    }

    override fun replaceFirst(source: CharSequence, replacement: CharSequence): CharSequence {
        return source.replaceFirst(getRule(source), replacement)
    }

    override fun replaceAll(source: CharSequence, replacement: CharSequence): CharSequence {
        return source.replaceAll(getRule(source), replacement)
    }

    override fun replaceFirst(source: CharSequence, replacementProvider: (T) -> Any): CharSequence {
        return source.replaceFirst(getRule(source), replacementProvider)
    }

    override fun replaceAll(source: CharSequence, replacementProvider: (T) -> Any): CharSequence {
        return source.replaceAll(getRule(source), replacementProvider)
    }

    override fun replaceFirstOrNull(source: CharSequence, replacement: CharSequence): CharSequence? {
        return source.replaceFirstOrNull(getRule(source), replacement)
    }

    override fun replaceFirstOrNull(source: CharSequence, replacementProvider: (T) -> CharSequence): CharSequence? {
        return source.replaceFirstOrNull(getRule(source), replacementProvider)
    }

    override fun startsWith(string: CharSequence): Boolean {
        return string.startsWith(getRule(string))
    }

    override fun endsWith(string: CharSequence): Boolean {
        return string.endsWith(getRule(string))
    }

    override fun indexOf(string: CharSequence): Int? {
        return string.indexOf(getRule(string))
    }

    override fun lastIndexOfShortestMatch(string: CharSequence): Int? {
        return string.lastIndexOfShortestMatch(getRule(string))
    }

    override fun lastIndexOfLongestMatch(string: CharSequence): Int? {
        return string.lastIndexOfLongestMatch(getRule(string))
    }

    override fun findFirstResult(string: CharSequence): ParseRangeResult<T>? {
        return string.findFirstResult(getRule(string))
    }

    override fun findFirstRange(string: CharSequence): ParseRange? {
        return string.findFirstRange(getRule(string))
    }

    override fun findFirst(string: CharSequence): T? {
        return string.findFirst(getRule(string))
    }

    override fun findAll(string: CharSequence): List<T> {
        return string.findAll(getRule(string))
    }

    override fun findAllResults(string: CharSequence): List<ParseRangeResult<T>> {
        return string.findAllResults(getRule(string))
    }

    override fun count(string: CharSequence): Int {
        return string.count(getRule(string))
    }
}
