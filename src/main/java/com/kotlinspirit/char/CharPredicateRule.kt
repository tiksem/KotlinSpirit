package com.kotlinspirit.char

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.CharPredicateResultRule
import com.kotlinspirit.str.StringCharPredicateRangeRule
import com.kotlinspirit.str.StringCharPredicateRule

open class CharPredicateRule : CharRule {
    internal val predicate: (Char) -> Boolean
    internal val data: CharPredicateData?
    internal val eofParseCode: Int

    internal constructor(
        data: CharPredicateData?,
        predicate: (Char) -> Boolean,
        eofParseCode: Int = ParseCode.EOF,
        name: String? = null
    ) : super(name) {
        this.data = data
        this.predicate = predicate
        this.eofParseCode = eofParseCode
    }

    internal constructor(data: CharPredicateData, eofParseCode: Int = ParseCode.EOF, name: String? = null) : super(name) {
        this.data = data
        this.predicate = data.toPredicate()
        this.eofParseCode = eofParseCode
    }

    internal constructor(predicate: (Char) -> Boolean, eofParseCode: Int = ParseCode.EOF, name: String? = null) : super(name) {
        this.predicate = predicate
        this.data = null
        this.eofParseCode = eofParseCode
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        if (seek >= string.length) {
            return createStepResult(
                seek = seek,
                parseCode = eofParseCode
            )
        }

        return if (predicate(string[seek])) {
            createComplete(seek + 1)
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        if (seek >= string.length) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = eofParseCode
            )
            return
        }

        val ch = string[seek]
        if (predicate(ch)) {
            result.data = ch
            result.parseResult = createComplete(seek + 1)
        } else {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < string.length && predicate(string[seek])
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        if (seek < 0) {
            return createStepResult(
                seek = seek,
                parseCode = eofParseCode
            )
        }

        return if (predicate(string[seek])) {
            createComplete(seek - 1)
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        if (seek < 0) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = eofParseCode
            )
            return
        }

        val ch = string[seek]
        if (predicate(ch)) {
            result.data = ch
            result.parseResult = createComplete(seek - 1)
        } else {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return seek >= 0 && predicate(string[seek])
    }

    override fun not(): CharPredicateRule {
        val predicate = this.predicate
        return CharPredicateRule(
            predicate = {
                !predicate(it)
            },
            eofParseCode = ParseCode.COMPLETE
        )
    }

    infix fun or(rule: CharPredicateRule): CharPredicateRule {
        val data = this.data
        val otherData = rule.data

        return if (data != null && otherData != null) {
            CharPredicateRule(data + otherData)
        } else {
            val thisPredicate = this.predicate
            val otherPredicate = rule.predicate
            CharPredicateRule(predicate = {
                thisPredicate(it) || otherPredicate(it)
            })
        }
    }

    operator fun minus(rule: CharPredicateRule): CharPredicateRule {
        val data = this.data
        val otherData = rule.data

        return if (data != null && otherData != null) {
            val resultData = data - otherData
            if (resultData.isExactChar()) {
                ExactCharRule(resultData.chars.first())
            } else {
                CharPredicateRule(resultData)
            }
        } else {
            val otherPredicate = rule.predicate
            val thisPredicate = this.predicate
            CharPredicateRule(predicate = {
                thisPredicate(it) && !otherPredicate(it)
            })
        }
    }

    override fun repeat(): StringCharPredicateRule {
        return StringCharPredicateRule(predicate)
    }

    override fun repeat(range: IntRange): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(predicate, range)
    }

    override fun unaryPlus(): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(predicate, range = 1..Int.MAX_VALUE)
    }

    override fun repeat(count: Int): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(predicate, range = count..count)
    }

    override fun invoke(callback: (Char) -> Unit): CharPredicateResultRule {
        return CharPredicateResultRule(rule = this, callback)
    }

    override fun clone(): CharPredicateRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() {
            val data = data
            if (data != null) {
                val chars = if (data.chars.isEmpty()) {
                    ""
                } else {
                    "(" + data.chars.joinToString(",") {
                        if (it == ',') "`,`" else it.toString()
                    } + ")"
                }
                val ranges = data.ranges.joinToString("") {
                    "[${it.first}..${it.last}]"
                }
                return "char$chars$ranges"
            } else {
                return "charWithCustomPredicate"
            }
        }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun name(name: String): Rule<Char> {
        return CharPredicateRule(data, predicate, eofParseCode, name)
    }

    override fun ignoreCallbacks(): CharPredicateRule {
        return this
    }
}
