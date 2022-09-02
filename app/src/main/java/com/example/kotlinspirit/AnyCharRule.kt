package com.example.kotlinspirit

class CharResultRule(
    rule: CharRule,
    callback: (Char) -> Unit
) : BaseRuleWithResult<Char>(rule, callback) {
    override fun repeat(): Rule<CharSequence> {
        return ZeroOrMoreRule(this).asStringRule()
    }

    override fun repeat(range: IntRange): Rule<CharSequence> {
        return RepeatRule(this, range).asStringRule()
    }

    override fun invoke(callback: (Char) -> Unit): CharResultRule {
        return CharResultRule(rule as CharRule, callback)
    }

    override fun clone(): CharResultRule {
        return CharResultRule(rule.clone() as CharRule, callback)
    }
}

abstract class CharRule : Rule<Char>() {
    override fun minus(rule: Rule<*>): CharDiffRule {
        return CharDiffRule(main = this, diff = rule)
    }

    override fun minus(string: String): CharDiffRule {
        return CharDiffRule(main = this, diff = Rules.str(string))
    }

    override fun minus(ch: Char): CharDiffRule {
        return CharDiffRule(main = this, diff = Rules.char(ch))
    }

    override fun invoke(callback: (Char) -> Unit): BaseRuleWithResult<Char> {
        return CharResultRule(this, callback)
    }

    override fun repeat(): Rule<CharSequence> {
        return ZeroOrMoreRule(this).asStringRule()
    }

    override fun repeat(range: IntRange): Rule<CharSequence> {
        return RepeatRule(this, range).asStringRule()
    }

    abstract override fun clone(): CharRule
}

class AnyCharRule : CharRule() {
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

    override fun repeat(): StringCharPredicateRule {
        //TODO: Optimise
        return StringCharPredicateRule { true }
    }

    override fun repeat(range: IntRange): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(predicate = {
            true
        }, range = range)
    }
}