package com.example.kotlinspirit

object Rules {
    val int get() = IntRule()

    fun char(vararg ch: Char): CharPredicateRule {
        return CharPredicateRule(
            CharPredicates.from(*ch)
        )
    }

    fun char(vararg range: CharRange): CharPredicateRule {
        return CharPredicateRule(
            CharPredicates.from(*range)
        )
    }

    fun char(chars: CharArray, ranges: Array<CharRange>): CharPredicateRule {
        return CharPredicateRule(
            CharPredicates.from(
                ranges, chars
            )
        )
    }

    fun nonEmptyStr(vararg ch: Char): StringOneOrMoreCharPredicateRule {
        return StringOneOrMoreCharPredicateRule(
            CharPredicates.from(*ch)
        )
    }

    fun nonEmptyStr(vararg range: CharRange): StringOneOrMoreCharPredicateRule {
        return StringOneOrMoreCharPredicateRule(
            CharPredicates.from(*range)
        )
    }

    fun nonEmptyStr(chars: CharArray, ranges: Array<CharRange>): StringOneOrMoreCharPredicateRule {
        return StringOneOrMoreCharPredicateRule(
            CharPredicates.from(
                ranges, chars
            )
        )
    }

    fun str(vararg ch: Char): StringCharPredicateRule {
        return StringCharPredicateRule(
            CharPredicates.from(*ch)
        )
    }

    fun str(vararg range: CharRange): StringCharPredicateRule {
        return StringCharPredicateRule(
            CharPredicates.from(*range)
        )
    }

    fun str(chars: CharArray, ranges: Array<CharRange>): StringCharPredicateRule {
        return StringCharPredicateRule(
            CharPredicates.from(
                ranges, chars
            )
        )
    }
}