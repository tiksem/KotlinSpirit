package com.kotlinspirit.core

import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.RuleDebugTreeNode
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult
import java.util.concurrent.ConcurrentHashMap

private data class DebugData(
    val root: RuleDebugTreeNode?,
    val history: List<RuleDebugTreeNode>
)

internal class DebugParser<T : Any>(private val parser: Parser<T>) : Parser<T> {
    private val debugData = ConcurrentHashMap<Long, DebugData>()

    private fun endSession() {
        debugData[Thread.currentThread().id] = DebugData(
            root = DebugEngine.root,
            history = DebugEngine.history
        )
    }

    override fun parseGetResultOrThrow(string: CharSequence): T {
        DebugEngine.startDebugSession(string)
        return parser.parseGetResultOrThrow(string).also {
            endSession()
        }
    }

    override fun parseOrThrow(string: CharSequence): Int {
        DebugEngine.startDebugSession(string)
        return parser.parseOrThrow(string).also {
            endSession()
        }
    }

    override fun tryParse(string: CharSequence): Int? {
        DebugEngine.startDebugSession(string)
        return parser.tryParse(string).also {
            endSession()
        }
    }

    override fun parseWithResult(string: CharSequence): ParseResult<T> {
        DebugEngine.startDebugSession(string)
        return parser.parseWithResult(string).also {
            endSession()
        }
    }

    override fun parse(string: CharSequence): ParseSeekResult {
        DebugEngine.startDebugSession(string)
        return parser.parse(string).also {
            endSession()
        }
    }

    override fun matches(string: CharSequence): Boolean {
        DebugEngine.startDebugSession(string)
        return parser.matches(string).also {
            endSession()
        }
    }

    override fun matchOrThrow(string: CharSequence) {
        DebugEngine.startDebugSession(string)
        return parser.matchOrThrow(string).also {
            endSession()
        }
    }

    override fun matchesAtBeginning(string: CharSequence): Boolean {
        DebugEngine.startDebugSession(string)
        return parser.matchesAtBeginning(string).also {
            endSession()
        }
    }

    override fun replaceFirst(source: CharSequence, replacement: CharSequence): CharSequence {
        DebugEngine.startDebugSession(source)
        return parser.replaceFirst(source, replacement).also {
            endSession()
        }
    }

    override fun replaceAll(source: CharSequence, replacement: CharSequence): CharSequence {
        DebugEngine.startDebugSession(source)
        return parser.replaceAll(source, replacement).also {
            endSession()
        }
    }

    override fun replaceFirst(source: CharSequence, replacementProvider: (T) -> Any): CharSequence {
        DebugEngine.startDebugSession(source)
        return parser.replaceFirst(source, replacementProvider).also {
            endSession()
        }
    }

    override fun replaceAll(source: CharSequence, replacementProvider: (T) -> Any): CharSequence {
        DebugEngine.startDebugSession(source)
        return parser.replaceAll(source, replacementProvider).also {
            endSession()
        }
    }

    override fun replaceFirstOrNull(source: CharSequence, replacement: CharSequence): CharSequence? {
        DebugEngine.startDebugSession(source)
        return parser.replaceFirstOrNull(source, replacement).also {
            endSession()
        }
    }

    override fun replaceFirstOrNull(source: CharSequence, replacementProvider: (T) -> CharSequence): CharSequence? {
        DebugEngine.startDebugSession(source)
        return parser.replaceFirstOrNull(source, replacementProvider).also {
            endSession()
        }
    }

    override fun indexOf(string: CharSequence): Int? {
        DebugEngine.startDebugSession(string)
        return parser.indexOf(string).also {
            endSession()
        }
    }

    override fun findFirstResult(string: CharSequence): ParseRangeResult<T>? {
        DebugEngine.startDebugSession(string)
        return parser.findFirstResult(string).also {
            endSession()
        }
    }

    override fun findFirst(string: CharSequence): T? {
        DebugEngine.startDebugSession(string)
        return parser.findFirst(string).also {
            endSession()
        }
    }

    override fun findFirstRange(string: CharSequence): ParseRange? {
        DebugEngine.startDebugSession(string)
        return parser.findFirstRange(string).also {
            endSession()
        }
    }

    override fun findAll(string: CharSequence): List<T> {
        DebugEngine.startDebugSession(string)
        return parser.findAll(string).also {
            endSession()
        }
    }

    override fun findAllResults(string: CharSequence): List<ParseRangeResult<T>> {
        DebugEngine.startDebugSession(string)
        return parser.findAllResults(string).also {
            endSession()
        }
    }

    override fun getDebugTree(): RuleDebugTreeNode? {
        return debugData[Thread.currentThread().id]?.root
    }

    override fun getDebugHistory(): List<RuleDebugTreeNode> {
        return debugData[Thread.currentThread().id]?.history ?: emptyList()
    }
}