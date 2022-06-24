package com.example.kotlinspirit

class AnyCharRule : Rule<Char>() {
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
        return if (seek >= string.length) {
            -seek
        } else {
            seek
        }
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

    override fun invoke(callback: (Char) -> Unit): BaseRuleWithResult<Char> {
        return object : BaseRuleWithResult<Char>(this, callback) {
            override fun repeat(): StringCharPredicateRule {
                return rule.repeat() as StringCharPredicateRule
            }

            override fun repeat(range: IntRange): StringCharPredicateRangeRule {
                return rule.repeat(range) as StringCharPredicateRangeRule
            }

            override fun invoke(callback: (Char) -> Unit): BaseRuleWithResult<Char> {
                return rule.invoke(callback)
            }
        }
    }
}