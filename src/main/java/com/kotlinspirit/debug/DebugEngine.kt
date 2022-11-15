package com.kotlinspirit.debug

import com.kotlinspirit.core.*
import com.kotlinspirit.core.getSeek
import com.kotlinspirit.core.isError
import com.kotlinspirit.core.parseCodeToString
import org.json.JSONArray
import org.json.JSONObject
import java.lang.IllegalStateException
import java.lang.RuntimeException
import java.util.concurrent.ConcurrentHashMap

private const val DEBUG_MAX_TOKEN_LENGTH = 20

class RuleDebugTreeNode(
    val rule: Rule<*>,
    val string: CharSequence
) {
    val name: String
        get() = (rule as? DebugRule)?.name ?: throw RuntimeException()
    var startSeek = -1
        internal set
    var endSeek = -1
        internal set
    var parseCode = -1
        internal set
    var parent: RuleDebugTreeNode? = null
        internal set
    private val children = ArrayList<RuleDebugTreeNode>()

    fun addChild(node: RuleDebugTreeNode) {
        children.add(node)
    }

    private fun toJson(): JSONObject {
        return JSONObject().also {
            it.put("name", name)
            it.put("parseCode", parseCode.parseCodeToString())
            if (parseCode.isError()) {
                var token = string.subSequence(0, startSeek)
                if (token.length > DEBUG_MAX_TOKEN_LENGTH) {
                    token = string.subSequence(startSeek - DEBUG_MAX_TOKEN_LENGTH, startSeek)
                }
                it.put("afterToken", token)
            }
            it.put("start", startSeek)
            it.put("end", endSeek)
            if (children.isNotEmpty()) {
                val arr = JSONArray().apply {
                    children.forEach { child ->
                        put(child.toJson())
                    }
                }
                it.put("nested", arr)
            }
        }
    }

    override fun toString(): String {
        return toJson().toString(4)
    }
}

class DebugEngine {
    var root: RuleDebugTreeNode? = null
        private set
    private val history = ArrayList<RuleDebugTreeNode>()
    private var seek: RuleDebugTreeNode? = null
    private var string: CharSequence = ""

    fun startDebugSession(string: CharSequence) {
        this.string = string
        history.clear()
        root = null
        seek = null
    }

    fun ruleParseStarted(rule: Rule<*>, startSeek: Int) {
        val node = RuleDebugTreeNode(rule, string).also {
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

    fun ruleParseEnded(rule: Rule<*>, result: Long) {
        val seek = this.seek
            ?: throw IllegalStateException("Undefined behaviour, " +
                    "ruleParseEnded was called before parsing was started")

        if (seek.rule != rule) {
            throw IllegalStateException("Undefined behaviour, ended rule doesn't match started rule")
        }

        seek.endSeek = result.getSeek()
        seek.parseCode = result.getParseCode()
        if (seek.parent == null) {
            history.add(seek)
        }

        this.seek = seek.parent
    }

    companion object {
        private val engines = ConcurrentHashMap<Long, DebugEngine>()

        private fun getEngine(): DebugEngine {
            return engines.getOrPut(Thread.currentThread().id) {
                DebugEngine()
            }
        }

        val root: RuleDebugTreeNode?
            get() = getEngine().root

        val history: List<RuleDebugTreeNode>
            get() = getEngine().history

        fun startDebugSession(string: CharSequence) {
            getEngine().startDebugSession(string)
        }

        fun ruleParseStarted(rule: Rule<*>, startSeek: Int) {
            getEngine().ruleParseStarted(rule, startSeek)
        }

        fun ruleParseEnded(rule: Rule<*>, result: Long) {
            getEngine().ruleParseEnded(rule, result)
        }
    }
}