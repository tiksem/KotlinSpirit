package com.kotlinspirit.core

import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.RuleDebugTreeNode

internal class DebugParser<T : Any>(private val parser: Parser<T>) : Parser<T> {
    override fun parseGetResultOrThrow(string: CharSequence): T {
        DebugEngine.startDebugSession(string)
        return parser.parseGetResultOrThrow(string)
    }

    override fun parseOrThrow(string: CharSequence): Int {
        DebugEngine.startDebugSession(string)
        return parser.parseOrThrow(string)
    }

    override fun tryParse(string: CharSequence): Int? {
        DebugEngine.startDebugSession(string)
        return parser.tryParse(string)
    }

    override fun parseWithResult(string: CharSequence): ParseResult<T> {
        DebugEngine.startDebugSession(string)
        return parser.parseWithResult(string)
    }

    override fun parse(string: CharSequence): ParseSeekResult {
        DebugEngine.startDebugSession(string)
        return parser.parse(string)
    }

    override fun matches(string: CharSequence): Boolean {
        DebugEngine.startDebugSession(string)
        return parser.matches(string)
    }

    override fun matchOrThrow(string: CharSequence) {
        DebugEngine.startDebugSession(string)
        return parser.matchOrThrow(string)
    }

    override fun matchesAtBeginning(string: CharSequence): Boolean {
        DebugEngine.startDebugSession(string)
        return parser.matchesAtBeginning(string)
    }

    override fun getDebugTree(): RuleDebugTreeNode? {
        return DebugEngine.root
    }

    override fun replaceFirst(source: CharSequence, replacement: CharSequence): CharSequence {
        DebugEngine.startDebugSession(source)
        return parser.replaceFirst(source, replacement)
    }

    override fun replaceAll(source: CharSequence, replacement: CharSequence): CharSequence {
        DebugEngine.startDebugSession(source)
        return parser.replaceAll(source, replacement)
    }
}