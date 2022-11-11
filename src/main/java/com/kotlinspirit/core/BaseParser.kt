package com.kotlinspirit.core

import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.ext.appendRange
import com.kotlinspirit.ext.replaceRanges

internal abstract class BaseParser<T : Any>(protected val originRule: Rule<T>) : Parser<T> {
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
        var seek = 0
        val length = source.length
        val rule = getRule()
        do {
            val result = rule.parse(seek, source)
            if (result.getParseCode().isNotError()) {
                return source.replaceRange(seek until result.getSeek(), replacement)
            }
            val newSeek = result.getSeek()
            if (newSeek == seek) {
                seek++
            } else {
                seek = newSeek
            }
        } while (seek < length)

        return source
    }

    override fun replaceAll(source: CharSequence, replacement: CharSequence): CharSequence {
        val ranges = ArrayList<IntRange>()

        var seek = 0
        val length = source.length
        val rule = getRule()
        do {
            val result = rule.parse(seek, source)
            if (result.getParseCode().isNotError()) {
                val range = seek until result.getSeek()
                ranges.add(range)
            }
            val newSeek = result.getSeek()
            if (newSeek == seek) {
                seek++
            } else {
                seek = newSeek
            }

        } while (seek < length)

        return source.replaceRanges(ranges, replacement)
    }
}