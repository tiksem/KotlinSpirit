package com.kotlinspirit.char

import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.core.*
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.core.BaseRuleWithResult
import com.kotlinspirit.expressive.CharDiffRule
import com.kotlinspirit.expressive.OptionalCharRule
import com.kotlinspirit.repeat.OneOrMoreRule
import com.kotlinspirit.repeat.RepeatRule
import com.kotlinspirit.repeat.ZeroOrMoreRule
import com.kotlinspirit.str.StringCharPredicateRangeRule
import com.kotlinspirit.str.StringCharPredicateRule
import com.kotlinspirit.str.StringOneOrMoreCharPredicateRule

open class CharResultRule(
    rule: CharRule,
    callback: (Char) -> Unit
) : BaseRuleWithResult<Char>(rule, callback) {
    override fun repeat(): Rule<CharSequence> {
        return ZeroOrMoreRule(this).asString()
    }

    override fun repeat(range: IntRange): Rule<CharSequence> {
        return RepeatRule(this, range).asString()
    }

    override fun unaryPlus(): Rule<CharSequence> {
        return OneOrMoreRule(this).asString()
    }

    override fun invoke(callback: (Char) -> Unit): CharResultRule {
        return CharResultRule(rule as CharRule, callback)
    }

    override fun clone(): CharResultRule {
        return CharResultRule(rule.clone() as CharRule, callback)
    }

    override fun debug(name: String?): CharResultRule {
        return CharResultRule(rule.internalDebug(name) as CharRule, callback)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = rule.debugNameShouldBeWrapped
}

abstract class CharRule : Rule<Char>() {
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

    override fun repeat(): Rule<CharSequence> {
        return ZeroOrMoreRule(this).asString()
    }

    override fun repeat(range: IntRange): Rule<CharSequence> {
        return RepeatRule(this, range).asString()
    }

    override fun unaryPlus(): Rule<CharSequence> {
        return OneOrMoreRule(this).asString()
    }

    override fun unaryMinus(): OptionalCharRule {
        return OptionalCharRule(this)
    }

    abstract override fun clone(): CharRule
    abstract override fun debug(name: String?): CharRule
}

open class AnyCharRule : CharRule() {
    override fun parse(seek: Int, string: CharSequence): Long {
        if (seek >= string.length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        return createStepResult(
            seek = seek + 1,
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        if (seek >= string.length) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            return
        }

        result.data = string[seek]
        result.parseResult = createStepResult(
            seek = seek + 1,
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < string.length
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return if (seek < string.length) {
            -seek - 1
        } else {
            seek
        }
    }

    override fun clone(): AnyCharRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): AnyCharRule {
        return DebugAnyCharRule(name ?: "char")
    }

    override fun repeat(): StringCharPredicateRule {
        //TODO: Optimise
        return StringCharPredicateRule { true }
    }

    override fun unaryPlus(): StringOneOrMoreCharPredicateRule {
        //TODO: Optimise
        return StringOneOrMoreCharPredicateRule { true }
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
}

private class DebugAnyCharRule(override val name: String) : AnyCharRule(), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}