package com.kotlinspirit.str.oneof

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class OneOfStringRule internal constructor(
    private val strings: List<CharSequence>,
    private val skipper: Rule<*>?,
    name: String? = null
) :
    RuleWithDefaultRepeat<CharSequence>(name) {

    private val tree = if (skipper == null) {
        TernarySearchTree(strings)
    } else {
        object : TernarySearchTree(strings) {
            override fun moveSeekToTheNextChar(seek: Int, string: CharSequence): Int {
                return skipper.parse(seek + 1, string).seek
            }
        }
    }
    // Reverse search is rarely used, that's why make it lazy to perform reversed search on when it's needed
    private val reversedTree by lazy(lock = this) {
        val reversedStrings = strings.map {
            it.reversed()
        }
        if (skipper == null) {
            TernarySearchTree(reversedStrings)
        } else {
            object : TernarySearchTree(reversedStrings) {
                override fun moveSeekToThePrevChar(seek: Int, string: CharSequence): Int {
                    return skipper.reverseParse(seek - 1, string).seek
                }
            }
        }
    }

    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        val result = tree.parse(seek, string)
        return if (result >= 0) {
            ParseSeekResult(
                seek = result,
                parseCode = ParseCode.COMPLETE
            )
        } else {
            ParseSeekResult(
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
            result.parseResult = ParseSeekResult(
                seek = r,
                parseCode = ParseCode.COMPLETE
            )
            result.data = string.subSequence(seek, r)
        } else {
            result.parseResult = ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.ONE_OF_STRING_NOT_FOUND
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return tree.hasMatch(seek, string)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        val result = reversedTree.reverseParse(seek, string)
        return if (result >= -1) {
            ParseSeekResult(
                seek = result,
                parseCode = ParseCode.COMPLETE
            )
        } else {
            ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.ONE_OF_STRING_NOT_FOUND
            )
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        val r = reversedTree.reverseParse(seek, string)
        if (r >= -1) {
            result.parseResult = ParseSeekResult(
                seek = r,
                parseCode = ParseCode.COMPLETE
            )
            result.data = string.subSequence(r + 1, seek + 1)
        } else {
            result.parseResult = ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.ONE_OF_STRING_NOT_FOUND
            )
            result.data = null
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return reversedTree.reverseHasMatch(seek, string)
    }

    override fun clone(): OneOfStringRule {
        return if (skipper == null) {
            this
        } else {
            OneOfStringRule(strings, skipper.clone(), name)
        }
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): OneOfStringRule {
        return OneOfStringRule(strings, skipper, name)
    }

    override val defaultDebugName: String
        get() {
            return strings.joinToString("|") {
                it.toString().replace("|", "`|`")
            }
        }

    override fun isThreadSafe(): Boolean {
        return this.skipper?.isThreadSafe() != false
    }
}