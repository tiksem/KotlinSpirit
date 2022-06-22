package com.example.kotlinspirit

import java.util.concurrent.ConcurrentHashMap

class Parser<T : Any>(
    private val rule: Rule<T>
) {
    private val threadMap = ConcurrentHashMap<Long, Rule<T>>()

    init {
        threadMap[rule.threadId] = rule
    }

    private fun getRule(): Rule<T> {
        val threadId = Thread.currentThread().id
        return threadMap.getOrPut(threadId) {
            rule.clone()
        }
    }

    fun parseGetResultOrThrow(string: CharSequence): T {
        val rule = getRule()
        val result = ParseResult<T>()
        rule.parseWithResult(0, string, result)
        val stepResult = result.stepResult
        if (stepResult.getStepCode().isError()) {
            throw ParseException(stepResult, string)
        } else {
            return result.data!!
        }
    }

    fun parseOrThrow(string: CharSequence): Int {
        val rule = getRule()
        val result = rule.parse(0, string)
        if (result.getStepCode().isError()) {
            throw ParseException(
                result, string
            )
        }

        return result.getSeek()
    }

    fun tryParse(string: CharSequence): Int? {
        val rule = getRule()
        val result = rule.parse(0, string)
        if (result.getStepCode().isError()) {
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
        val rule = getRule()
        val result = rule.parse(0, string)
        if (result.getStepCode().isError()) {
            throw ParseException(result, string)
        }

        val seek = result.getSeek()
        if (seek != string.length) {
            throw ParseException(
                result = createStepResult(
                    seek = seek,
                    stepCode = StepCode.WHOLE_STRING_DOES_NOT_MATCH
                ),
                string = string
            )
        }
    }

    fun matches(string: CharSequence): Boolean {
        val rule = getRule()
        val result = rule.parse(0, string)
        return result.getStepCode().isNotError() && result.getSeek() == string.length
    }

    fun matchesAtBeginning(string: CharSequence): Boolean {
        val rule = getRule()
        return rule.hasMatch(0, string)
    }
}