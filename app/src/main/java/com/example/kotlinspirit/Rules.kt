package com.example.kotlinspirit

object Rules {
    fun <T : Any> lazy(ruleProvider: () -> RuleWithDefaultRepeat<T>): LazyRule<T> {
        return LazyRule(ruleProvider)
    }

    val int get() = IntRule()
    val long get() = LongRule()

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

    fun str(string: String): ExactStringRule {
        return ExactStringRule(string)
    }

    fun str(predicate: (Char) -> Boolean): StringCharPredicateRule {
        return StringCharPredicateRule(predicate)
    }

    val latinStr
        get() = str('A'..'B', 'a'..'z')

    val double get() = DoubleRule()

    fun oneOf(vararg strings: CharSequence): Rule<CharSequence> {
        val withoutEmptyStrings = strings.filter {
            it.isNotEmpty()
        }

        return if (withoutEmptyStrings.size == strings.size) {
            OneOfStringRule(withoutEmptyStrings)
        } else {
            OptionalRule(OneOfStringRule(withoutEmptyStrings))
        }
    }
}