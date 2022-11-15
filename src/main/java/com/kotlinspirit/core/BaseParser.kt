package com.kotlinspirit.core

import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.ext.replaceRanges
import com.kotlinspirit.ext.toCharSequence
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
        val rule = getRule()
        val result = rule.parse(0, string)
        return result.getParseCode().isNotError() && result.getSeek() == string.length
    }

    override fun matchesAtBeginning(string: CharSequence): Boolean {
        return getRule().hasMatch(0, string)
    }

    override fun replaceFirst(source: CharSequence, replacement: CharSequence): CharSequence {
        getRule().findFirstSuccessfulSeek(source) { start, end ->
            return source.replaceRange(start until end, replacement)
        }

        return source
    }

    override fun replaceAll(source: CharSequence, replacement: CharSequence): CharSequence {
        val ranges = ArrayList<ParseRange>()

        getRule().findSuccessfulRanges(source) { start, end ->
            val range = ParseRange(start, end)
            ranges.add(range)
        }

        return source.replaceRanges(ranges, replacement)
    }

    override fun replaceFirst(source: CharSequence, replacementProvider: (T) -> Any): CharSequence {
        getRule().findFirstSuccessfulResult(source) { start, result ->
            return source.replaceRange(
                range = start until result.endSeek,
                replacement = replacementProvider(result.data!!).toCharSequence()
            )
        }

        return source
    }

    override fun replaceAll(source: CharSequence, replacementProvider: (T) -> Any): CharSequence {
        val ranges = ArrayList<ParseRange>()
        val replacements = ArrayList<CharSequence>()

        getRule().findSuccessfulResults(source) { start, end, value ->
            val range = ParseRange(start, end)
            ranges.add(range)
            replacements.add(replacementProvider(value).toCharSequence())
        }

        return source.replaceRanges(ranges, replacements)
    }

    override fun replaceFirstOrNull(source: CharSequence, replacement: CharSequence): CharSequence? {
        getRule().findFirstSuccessfulSeek(source) { start, end ->
            return source.replaceRange(start until end, replacement)
        }

        return null
    }

    override fun replaceFirstOrNull(source: CharSequence, replacementProvider: (T) -> CharSequence): CharSequence? {
        getRule().findFirstSuccessfulResult(source) { start, result ->
            return source.replaceRange(
                range = start until result.endSeek,
                replacement = replacementProvider(result.data!!)
            )
        }

        return null
    }

    override fun indexOf(string: CharSequence): Int? {
        getRule().findFirstSuccessfulSeek(string) { start, _ ->
            return start
        }

        return null
    }

    override fun findFirstResult(string: CharSequence): ParseRangeResult<T>? {
        getRule().findFirstSuccessfulResult(string) { start, result ->
            return ParseRangeResult(
                startSeek = start,
                endSeek = result.endSeek,
                data = result.data
            )
        }

        return null
    }

    override fun findFirstRange(string: CharSequence): ParseRange? {
        getRule().findFirstSuccessfulSeek(string) { start, end ->
            return ParseRange(start, end)
        }

        return null
    }

    override fun findFirst(string: CharSequence): T? {
        getRule().findFirstSuccessfulResult(string) { start, result ->
            return result.data
        }

        return null
    }

    override fun findAll(string: CharSequence): List<T> {
        val result = ArrayList<T>()
        getRule().findSuccessfulResults(string) { start, end, value ->
            result.add(value)
        }

        return result
    }

    override fun findAllResults(string: CharSequence): List<ParseRangeResult<T>> {
        val result = ArrayList<ParseRangeResult<T>>()
        getRule().findSuccessfulResults(string) { start, end, value ->
            result.add(ParseRangeResult(data = value, startSeek = start, endSeek = end))
        }

        return result
    }
}