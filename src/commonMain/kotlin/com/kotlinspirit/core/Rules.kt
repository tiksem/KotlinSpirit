package com.kotlinspirit.core

import com.kotlinspirit.char.*
import com.kotlinspirit.char.CharPredicateData
import com.kotlinspirit.char.CharPredicates
import com.kotlinspirit.expressive.*
import com.kotlinspirit.number.*
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.str.EmptyStringRule
import com.kotlinspirit.str.ExactStringRule
import com.kotlinspirit.str.StringCharPredicateRule
import com.kotlinspirit.str.StringOneOrMoreCharPredicateRule
import com.kotlinspirit.str.oneof.OneOfStringRule

object Rules {
    fun <T : Any> lazy(ruleProvider: () -> Rule<T>): LazyRule<T> {
        return LazyRule(ruleProvider)
    }

    val int = IntRule()
    val uint = UIntRule()
    val ulong = ULongRule()
    val long = LongRule()
    val short = ShortRule()
    val ushort = UShortRule()
    val bigint = BigIntegerRule()
    val bigDecimal = BigDecimalRule()

    val char = AnyCharRule()
    val digit = char('0'..'9')
    val space: CharPredicateRule = charIf {
        it.isWhitespace()
    }

    fun char(char: Char, vararg chars: Char): CharPredicateRule {
        if (chars.isEmpty()) {
            return ExactCharRule(char)
        }

        return CharPredicateRule(
            CharPredicateData(charArrayOf(char) + chars)
        )
    }

    fun char(vararg ranges: CharRange): CharPredicateRule {
        return CharPredicateRule(
            CharPredicateData(ranges)
        )
    }

    fun char(chars: CharArray, ranges: Array<CharRange>): CharPredicateRule {
        return CharPredicateRule(
            CharPredicateData(
                chars = chars,
                ranges = ranges.toList()
            )
        )
    }

    fun charIf(predicate: (Char) -> Boolean): CharPredicateRule {
        return CharPredicateRule(predicate)
    }

    fun nonEmptyStr(vararg chars: Char): StringOneOrMoreCharPredicateRule {
        return StringOneOrMoreCharPredicateRule(
            CharPredicates.from(*chars)
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
        return if (string.isEmpty()) {
            EmptyStringRule()
        } else {
            ExactStringRule(string)
        }
    }

    fun str(predicate: (Char) -> Boolean): StringCharPredicateRule {
        return StringCharPredicateRule(predicate)
    }

    val latinStr
        get() = str('A'..'B', 'a'..'z')

    val double get() = DoubleRule()
    val float get() = FloatRule()

    fun oneOf(vararg strings: CharSequence): RuleWithDefaultRepeat<CharSequence> {
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