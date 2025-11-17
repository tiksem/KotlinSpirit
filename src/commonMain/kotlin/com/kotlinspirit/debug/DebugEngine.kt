package com.kotlinspirit.debug

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.parseCodeToString

private const val DEBUG_MAX_TOKEN_LENGTH = 20

class RuleDebugTreeNode(
    val rule: Rule<*>,
    val string: CharSequence,
    val isReverse: Boolean
) {
    val name: String
        get() = (rule as? DebugRule)?.name ?: throw RuntimeException()
    var startSeek = -1
        internal set
    var endSeek = -1
        internal set
    var parseCode = -1
        internal set
    var data: Any? = null
        internal set
    var parent: RuleDebugTreeNode? = null
        internal set
    private val children = ArrayList<RuleDebugTreeNode>()

    fun addChild(node: RuleDebugTreeNode) {
        children.add(node)
    }

    private fun toJson(): Map<String, Any?> {
        return buildMap {
            put("name", name)
            put("parseCode", parseCode.parseCodeToString())
            if (data != null) {
                put("result", data)
            }
            if (parseCode != ParseCode.COMPLETE) {
                var token = string.subSequence(0, startSeek)
                if (token.length > DEBUG_MAX_TOKEN_LENGTH) {
                    token = string.subSequence(startSeek - DEBUG_MAX_TOKEN_LENGTH, startSeek)
                }
                put("afterToken", token.toString())
            }
            put("start", startSeek)
            put("end", endSeek)
            put("isReverse", isReverse)
            if (children.isNotEmpty()) {
                put("nested", children.map { it.toJson() })
            }
        }
    }

    private fun Map<String, Any?>.toJsonString(indent: Int = 0): String {
        val indentStr = "    ".repeat(indent)
        val nextIndentStr = "    ".repeat(indent + 1)

        return buildString {
            appendLine("{")
            val entries = this@toJsonString.entries.toList()
            entries.forEachIndexed { index, (key, value) ->
                append(nextIndentStr)
                append("\"$key\": ")
                when (value) {
                    is Map<*, *> -> {
                        @Suppress("UNCHECKED_CAST")
                        append((value as Map<String, Any?>).toJsonString(indent + 1))
                    }
                    is List<*> -> {
                        appendLine("[")
                        value.forEachIndexed { listIndex, item ->
                            append("    ".repeat(indent + 2))
                            if (item is Map<*, *>) {
                                @Suppress("UNCHECKED_CAST")
                                append((item as Map<String, Any?>).toJsonString(indent + 2))
                            } else {
                                append(jsonValue(item))
                            }
                            if (listIndex < value.size - 1) appendLine(",")
                            else appendLine()
                        }
                        append(nextIndentStr)
                        append("]")
                    }
                    else -> append(jsonValue(value))
                }
                if (index < entries.size - 1) appendLine(",")
                else appendLine()
            }
            append(indentStr)
            append("}")
        }
    }

    private fun jsonValue(value: Any?): String {
        return when (value) {
            null -> "null"
            is String -> "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""
            is Number -> value.toString()
            is Boolean -> value.toString()
            else -> "\"$value\""
        }
    }

    override fun toString(): String {
        return toJson().toJsonString()
    }
}

internal class DebugEngine {
    var root: RuleDebugTreeNode? = null
        private set
    val history = ArrayList<RuleDebugTreeNode>()
    private var seek: RuleDebugTreeNode? = null
    private var string: CharSequence = ""

    fun startDebugSession(string: CharSequence) {
        this.string = string
        history.clear()
        root = null
        seek = null
    }

    fun ruleParseStarted(rule: Rule<*>, startSeek: Int, isReverse: Boolean) {
        val node = RuleDebugTreeNode(rule, string, isReverse).also {
            it.startSeek = startSeek
        }
        if (seek == null) {
            root = node
            seek = root
        } else {
            seek?.addChild(node)
            node.parent = seek
            seek = node
        }
    }

    fun ruleParseEnded(rule: Rule<*>, result: ParseSeekResult, data: Any? = null) {
        val seek = this.seek
            ?: throw IllegalStateException("Undefined behaviour, " +
                    "ruleParseEnded was called before parsing was started")

        if (seek.rule != rule) {
            throw IllegalStateException("Undefined behaviour, ended rule doesn't match started rule")
        }

        seek.endSeek = result.seek
        seek.parseCode = result.parseCode
        seek.data = data
        if (seek.parent == null) {
            history.add(seek)
        }

        this.seek = seek.parent
    }
}