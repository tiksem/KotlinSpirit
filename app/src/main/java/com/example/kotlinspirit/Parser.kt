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
        val rule = getRule()
        val isDebug = rule is DebugRule
        if (isDebug) {
            DebugEngine.startDebugSession(string)
        }
        try {
            rule.parseWithResult(0, string, result)
            val stepResult = result.parseResult
            if (stepResult.getParseCode().isError()) {
                throw ParseException(
                    stepResult, string,
                    if (isDebug) DebugEngine.root else null
                )
            } else {
                return result.data!!
            }
        } finally {
            DebugEngine.endDebugSession()
        }
    }

    fun parseOrThrow(string: CharSequence): Int {
        val rule = getRule()
        val isDebug = rule is DebugRule
        if (isDebug) {
            DebugEngine.startDebugSession(string)
        }
        try {
            val result = rule.parse(0, string)
            if (result.getParseCode().isError()) {
                throw ParseException(
                    result, string,
                    if (isDebug) DebugEngine.root else null
                )
            }

            return result.getSeek()
        } finally {
            DebugEngine.endDebugSession()
        }
    }

    fun tryParse(string: CharSequence): Int? {
        val rule = getRule()
        val isDebug = rule is DebugRule
        if (isDebug) {
            DebugEngine.startDebugSession(string)
        }
        try {
            val result = rule.parse(0, string)
            if (result.getParseCode().isError()) {
                return null
            }

            return result.getSeek()
        } finally {
            DebugEngine.endDebugSession()
        }
    }

    fun parseWithResult(string: CharSequence): ParseResult<T> {
        val result = ParseResult<T>()
        val rule = getRule()
        if (rule is DebugRule) {
            DebugEngine.startDebugSession(string)
        }
        try {
            rule.parseWithResult(0, string, result)
            return result
        } finally {
            DebugEngine.endDebugSession()
        }
    }

    fun parse(string: CharSequence): ParseSeekResult {
        val rule = getRule()
        if (rule is DebugRule) {
            DebugEngine.startDebugSession(string)
        }
        try {
            val result = rule.parse(0, string)
            return ParseSeekResult(stepResult = result)
        } finally {
            DebugEngine.endDebugSession()
        }
    }

    fun matchOrThrow(string: CharSequence) {
        val rule = getRule()
        val isDebug = rule is DebugRule
        if (isDebug) {
            DebugEngine.startDebugSession(string)
        }
        try {
            val result = rule.parse(0, string)
            if (result.getParseCode().isError()) {
                throw ParseException(
                    result, string,
                    if (isDebug) DebugEngine.root else null
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
                    debugTree = if (isDebug) DebugEngine.root else null
                )
            }
        } finally {
            DebugEngine.endDebugSession()
        }
    }

    fun matches(string: CharSequence): Boolean {
        val rule = getRule()
        if (rule is DebugRule) {
            DebugEngine.startDebugSession(string)
        }
        try {
            val result = rule.parse(0, string)
            return result.getParseCode().isNotError() && result.getSeek() == string.length
        } finally {
            DebugEngine.endDebugSession()
        }
    }

    fun matchesAtBeginning(string: CharSequence): Boolean {
        return getRule().hasMatch(0, string)
    }

    fun getDebugTree(): RuleDebugTreeNode? {
        return DebugEngine.root
    }
}