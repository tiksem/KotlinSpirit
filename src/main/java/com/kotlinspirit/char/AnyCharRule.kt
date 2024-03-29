package com.kotlinspirit.char

import com.kotlinspirit.core.*
import com.kotlinspirit.expressive.CharDiffRule
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult
import com.kotlinspirit.rangeres.callbacks.RangeResultCharCallbacksRule
import com.kotlinspirit.rangeres.result.RangeResultCharCallbacksResultRule
import com.kotlinspirit.rangeres.result.RangeResultCharResultRule
import com.kotlinspirit.rangeres.simple.RangeResultCharRule
import com.kotlinspirit.repeat.RepeatRule
import com.kotlinspirit.str.StringCharPredicateRangeRule
import com.kotlinspirit.str.StringCharPredicateRule

class CharResultRule(
    rule: CharRule,
    callback: (Char) -> Unit,
    name: String? = null
) : BaseRuleWithResult<Char>(rule, callback, name) {
    override fun repeat(): Rule<CharSequence> {
        return RepeatRule(this, range = 0..Int.MAX_VALUE).asString()
    }

    override fun repeat(range: IntRange): Rule<CharSequence> {
        return RepeatRule(this, range).asString()
    }

    override fun repeat(count: Int): Rule<CharSequence> {
        return RepeatRule(this, range = count..count).asString()
    }

    override fun unaryPlus(): Rule<CharSequence> {
        return RepeatRule(this, range = 1..Int.MAX_VALUE).asString()
    }

    override fun getRange(out: ParseRange): CharRule {
        return RangeResultCharRule(this, out)
    }

    override fun getRange(callback: (ParseRange) -> Unit): CharRule {
        return RangeResultCharCallbacksRule(this, callback)
    }

    override fun getRangeResult(out: ParseRangeResult<Char>): CharRule {
        return RangeResultCharResultRule(this, out)
    }

    override fun getRangeResult(callback: (ParseRangeResult<Char>) -> Unit): CharRule {
        return RangeResultCharCallbacksResultRule(this, callback)
    }

    override fun invoke(callback: (Char) -> Unit): CharResultRule {
        return CharResultRule(rule as CharRule, callback)
    }

    override fun clone(): CharResultRule {
        return CharResultRule(rule.clone() as CharRule, callback, name)
    }

    override fun name(name: String): CharResultRule {
        return CharResultRule(rule as CharRule, callback, name)
    }

    override val defaultDebugName: String
        get() = "charResult"

    override val debugNameShouldBeWrapped: Boolean
        get() = rule.debugNameShouldBeWrapped
}

abstract class CharRule(name: String?) : RuleWithDefaultSequenceBehavior<Char>(name) {
    override fun minus(rule: Rule<*>): CharDiffRule {
        return CharDiffRule(main = this, diff = rule)
    }

    override fun minus(string: String): CharDiffRule {
        return CharDiffRule(main = this, diff = Rules.str(string))
    }

    override fun minus(ch: Char): CharRule {
        return CharDiffRule(main = this, diff = Rules.char(ch))
    }

    override fun invoke(callback: (Char) -> Unit): BaseRuleWithResult<Char> {
        return CharResultRule(this, callback)
    }

    override fun getRange(out: ParseRange): CharRule {
        return RangeResultCharRule(rule = this, outRange = out)
    }

    override fun getRange(callback: (ParseRange) -> Unit): CharRule {
        return RangeResultCharCallbacksRule(rule = this, callback)
    }

    override fun getRangeResult(out: ParseRangeResult<Char>): CharRule {
        return RangeResultCharResultRule(this, out)
    }

    override fun getRangeResult(callback: (ParseRangeResult<Char>) -> Unit): CharRule {
        return RangeResultCharCallbacksResultRule(this, callback)
    }

    override fun repeat(): Rule<CharSequence> {
        return RepeatRule(this, range = 0..Int.MAX_VALUE).asString()
    }

    override fun repeat(range: IntRange): Rule<CharSequence> {
        return RepeatRule(this, range).asString()
    }

    override fun unaryPlus(): Rule<CharSequence> {
        return RepeatRule(this, range = 1..Int.MAX_VALUE).asString()
    }

    override fun repeat(count: Int): Rule<*> {
        return RepeatRule(this, range = count..count).asString()
    }

    abstract override fun clone(): CharRule
}

open class AnyCharRule(name: String? = null) : CharRule(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        if (seek >= string.length) {
            return ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        return ParseSeekResult(
            seek = seek + 1,
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        if (seek >= string.length) {
            result.parseResult = ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            result.data = null
            return
        }

        result.data = string[seek]
        result.parseResult = ParseSeekResult(
            seek = seek + 1,
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < string.length
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        if (seek < 0) {
            return ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        return ParseSeekResult(
            seek = seek - 1,
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        if (seek < 0) {
            result.parseResult = ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            result.data = null
            return
        }

        result.data = string[seek]
        result.parseResult = ParseSeekResult(
            seek = seek - 1,
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return seek >= 0
    }

    override fun clone(): AnyCharRule {
        return this
    }

    override fun repeat(): StringCharPredicateRule {
        //TODO: Optimise
        return StringCharPredicateRule(predicate = { true })
    }

    override fun repeat(count: Int): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(predicate = { true }, range = count..count)
    }

    override fun unaryPlus(): StringCharPredicateRangeRule {
        //TODO: Optimise
        return StringCharPredicateRangeRule(predicate = { true }, range = 1..Int.MAX_VALUE)
    }

    override fun repeat(range: IntRange): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(predicate = {
            true
        }, range = range)
    }

    override operator fun minus(ch: Char): CharPredicateRule {
        return CharPredicateRule(predicate = {
            it != ch
        })
    }

    open operator fun minus(rule: CharPredicateRule): CharPredicateRule {
        val predicate = rule.predicate
        return CharPredicateRule(predicate = {
            !predicate(it)
        })
    }

    override fun isThreadSafe(): Boolean {
        return true
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): AnyCharRule {
        return AnyCharRule(name)
    }

    override val defaultDebugName: String
        get() = "char"
}