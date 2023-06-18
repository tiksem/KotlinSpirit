package com.kotlinspirit.str.oneof

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.str.ExactStringRule

class OneOfStringRule internal constructor(private val strings: List<CharSequence>, name: String? = null) :
    RuleWithDefaultRepeat<CharSequence>(name) {

    private val tree = TernarySearchTree(strings)
    // Reverse search is rarely used, that's why make it lazy to perform reversed search on when it's needed
    private val reversedTree by lazy(lock = this) {
        TernarySearchTree(strings.map {
            it.reversed()
        })
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

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        val result = reversedTree.reverseParse(seek, string)
        return if (result >= -1) {
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

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        val r = reversedTree.reverseParse(seek, string)
        if (r >= -1) {
            result.parseResult = createStepResult(
                seek = r,
                parseCode = ParseCode.COMPLETE
            )
            result.data = string.subSequence(r + 1, seek + 1)
        } else {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.ONE_OF_STRING_NOT_FOUND
            )
            result.data = null
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return reversedTree.reverseHasMatch(seek, string)
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

    override fun name(name: String): OneOfStringRule {
        return OneOfStringRule(strings, name)
    }

    override val defaultDebugName: String
        get() {
            return strings.joinToString("|") {
                it.toString().replace("|", "`|`")
            }
        }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun ignoreCallbacks(): OneOfStringRule {
        return this
    }
}