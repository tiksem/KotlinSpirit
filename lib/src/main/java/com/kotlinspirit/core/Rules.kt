package com.kotlinspirit.core

import com.kotlinspirit.char.*
import com.kotlinspirit.char.CharPredicateData
import com.kotlinspirit.char.CharPredicates
import com.kotlinspirit.expressive.LazyRule
import com.kotlinspirit.expressive.OptionalRule
import com.kotlinspirit.number.DoubleRule
import com.kotlinspirit.number.FloatRule
import com.kotlinspirit.number.IntRule
import com.kotlinspirit.number.LongRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.str.ExactStringRule
import com.kotlinspirit.str.StringCharPredicateRule
import com.kotlinspirit.str.StringOneOrMoreCharPredicateRule
import com.kotlinspirit.str.oneof.OneOfStringRule
import java.lang.IllegalArgumentException

object Rules {
    fun <T : Any> lazy(ruleProvider: () -> RuleWithDefaultRepeat<T>): LazyRule<T> {
        return LazyRule(ruleProvider)
    }

    val int get() = IntRule()
    val long get() = LongRule()
    val char get() = AnyCharRule()
    val digit = char('0'..'9')
    val space: CharPredicateRule = charIf {
        it.isWhitespace()
    }

    fun char(vararg chars: Char): CharPredicateRule {
        if (chars.isEmpty()) {
            throw IllegalArgumentException("char(...) chars should not be empty")
        }

        if (chars.size == 1) {
            return ExactCharRule(chars[0])
        }

        return CharPredicateRule(
            CharPredicateData(chars)
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
                chars = chars.toSortedSet(),
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
        return ExactStringRule(string)
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