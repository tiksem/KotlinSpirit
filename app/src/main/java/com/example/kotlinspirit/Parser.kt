package com.example.kotlinspirit

import java.util.concurrent.ConcurrentHashMap

class Parser<T : Any> internal constructor(private val originRule: Rule<T>) {
    private val ruleMap = ConcurrentHashMap<Thread, Rule<T>>()

    private fun getRule(): Rule<T> {
        return ruleMap.getOrPut(Thread.currentThread()) {
            originRule.clone()
        }
    }

    fun parseGetResultOrThrow(string: CharSequence): T {
        val result = ParseResult<T>()
        getRule().parseWithResult(0, string, result)
        val stepResult = result.parseResult
        if (stepResult.getParseCode().isError()) {
            throw ParseException(stepResult, string)
        } else {
            return result.data!!
        }
    }

    fun parseOrThrow(string: CharSequence): Int {
        val result = getRule().parse(0, string)
        if (result.getParseCode().isError()) {
            throw ParseException(
                result, string
            )
        }

        return result.getSeek()
    }

    fun tryParse(string: CharSequence): Int? {
        val result = getRule().parse(0, string)
        if (result.getParseCode().isError()) {
            return null
        }

        return result.getSeek()
    }

    fun parseWithResult(string: CharSequence): ParseResult<T> {
        val result = ParseResult<T>()
        getRule().parseWithResult(0, string, result)
        return result
    }

    fun parse(string: CharSequence): ParseSeekResult {
        val result = getRule().parse(0, string)
        return ParseSeekResult(stepResult = result)
    }

    fun matchOrThrow(string: CharSequence) {
        val result = getRule().parse(0, string)
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

    fun matches(string: CharSequence): Boolean {
        val result = getRule().parse(0, string)
        return result.getParseCode().isNotError() && result.getSeek() == string.length
    }

    fun matchesAtBeginning(string: CharSequence): Boolean {
        return getRule().hasMatch(0, string)
    }
}