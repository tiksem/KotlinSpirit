package com.kotlinspirit.str.oneof

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.str.ExactStringRule

open class OneOfStringRule internal constructor(private val strings: List<CharSequence>) :
    RuleWithDefaultRepeat<CharSequence>() {

    private var tree: TernarySearchTree

    init {
        tree = TernarySearchTree(strings)
    }

    internal constructor(strings: List<CharSequence>, tree: TernarySearchTree) : this(strings) {
        this.tree = tree
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        val result = tree.parse(seek, string)
        return if (result >= 0) {
            createStepResult(
                seek = result,
                parseCode = ParseCode.COMPLETE
            )
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.ONE_OF_STRING_NOT_FOUND
            )
        }
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<CharSequence>
    ) {
        val r = tree.parse(seek, string)
        if (r >= 0) {
            result.parseResult = createStepResult(
                seek = r,
                parseCode = ParseCode.COMPLETE
            )
            result.data = string.subSequence(seek, r)
        } else {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.ONE_OF_STRING_NOT_FOUND
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return tree.hasMatch(seek, string)
    }

    override infix fun or(string: String): OneOfStringRule {
        return OneOfStringRule(listOf(string) + strings)
    }

    infix fun or(anotherRule: ExactStringRule): OneOfStringRule {
        return OneOfStringRule(listOf(anotherRule.string) + strings)
    }

    infix fun or(anotherRule: OneOfStringRule): OneOfStringRule {
        return OneOfStringRule(anotherRule.strings + strings)
    }

    override fun clone(): OneOfStringRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    private fun generateDebugName(): String {
        return strings.joinToString("|") {
            it.toString().replace("|", "`|`")
        }
    }

    override fun debug(name: String?): OneOfStringRule {
        return DebugOneOfStringRule(name ?: generateDebugName(), strings, tree)
    }

    override fun isThreadSafe(): Boolean {
        return false
    }

    override fun ignoreCallbacks(): OneOfStringRule {
        return this
    }
}

private class DebugOneOfStringRule(
    override val name: String,
    strings: List<CharSequence>,
    tree: TernarySearchTree
) : OneOfStringRule(strings, tree), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<CharSequence>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}