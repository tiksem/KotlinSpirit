package com.kotlinspirit.core

import com.kotlinspirit.bool.BooleanRule
import com.kotlinspirit.char.*
import com.kotlinspirit.char.CharPredicateData
import com.kotlinspirit.char.CharPredicates
import com.kotlinspirit.dynamic.DynamicRule
import com.kotlinspirit.dynamic.DynamicStringRule
import com.kotlinspirit.eof.EndRule
import com.kotlinspirit.expressive.*
import com.kotlinspirit.grammar.GrammarRule
import com.kotlinspirit.json.JsonArrayRule
import com.kotlinspirit.json.JsonObjectRule
import com.kotlinspirit.move.AfterFirstMatchOfRule
import com.kotlinspirit.number.*
import com.kotlinspirit.regexp.RegexpRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.result.GroupSequenceRule
import com.kotlinspirit.result.ResultSequenceRule
import com.kotlinspirit.start.StartRule
import com.kotlinspirit.str.EmptyStringRule
import com.kotlinspirit.str.ExactStringRule
import com.kotlinspirit.str.StringCharPredicateRangeRule
import com.kotlinspirit.str.StringCharPredicateRule
import com.kotlinspirit.str.oneof.OneOfStringRule
import com.kotlinspirit.str.oneof.OneOfStringRuleCaseInsensetive
import com.kotlinspirit.transform.TransformRule
import kotlin.math.abs

interface Clearable {
    fun clear()
}

class NullBox<T>(
    var value: T?
) : Clearable {
    override fun clear() {
        value = null
    }
}

class Box<T>(
    var value: T
)

object Rules {
    fun <T : Any> lazy(ruleProvider: () -> Rule<T>): Rule<T> {
        return grammar(
            dataFactory = { NullBox<T>(null) },
            defineRule = {
                val rule = ruleProvider()
                rule.invoke { value ->
                    it.value = value
                }
            },
            getResult = {
                it.value ?: throw IllegalStateException(
                    "Result is not set. Rule was not parsed yet."
                )
            }
        )
    }

    val int = IntRule(radix = 10)
    val uint = UIntRule()
    val ulong = ULongRule()
    val long = LongRule(radix = 10)
    val short = ShortRule(radix = 10)
    val ushort = UShortRule()
    val bigint = BigIntegerRule()
    val bigDecimal = BigDecimalRule()
    val byte = ByteRule(radix = 10)
    val ubyte = UByteRule()

    fun int(value: Int): RuleWithDefaultRepeat<Int> {
        return int.failIf { it != value }
    }

    fun int(range: IntRange): RuleWithDefaultRepeat<Int> {
        return int.failIf { it !in range }
    }

    fun long(value: Long): RuleWithDefaultRepeat<Long> {
        return long.failIf { it != value }
    }

    fun long(range: LongRange): RuleWithDefaultRepeat<Long> {
        return long.failIf { it !in range }
    }

    fun short(value: Short): RuleWithDefaultRepeat<Short> {
        return short.failIf { it != value }
    }

    fun short(value: IntRange): RuleWithDefaultRepeat<Short> {
        return short.failIf { it !in value }
    }

    fun byte(value: Byte): RuleWithDefaultRepeat<Byte> {
        return byte.failIf { it != value }
    }

    fun byte(value: IntRange): RuleWithDefaultRepeat<Byte> {
        return byte.failIf { it !in value }
    }

    fun uint(value: UInt): RuleWithDefaultRepeat<UInt> {
        return uint.failIf { it != value }
    }

    fun uint(value: UIntRange): RuleWithDefaultRepeat<UInt> {
        return uint.failIf { it !in value }
    }

    fun ulong(value: ULong): RuleWithDefaultRepeat<ULong> {
        return ulong.failIf { it != value }
    }

    fun ulong(value: ULongRange): RuleWithDefaultRepeat<ULong> {
        return ulong.failIf { it !in value }
    }

    fun ushort(value: UShort): RuleWithDefaultRepeat<UShort> {
        return ushort.failIf { it != value }
    }

    fun ushort(value: UIntRange): RuleWithDefaultRepeat<UShort> {
        return ushort.failIf { it !in value }
    }

    fun ubyte(value: UByte): RuleWithDefaultRepeat<UByte> {
        return ubyte.failIf { it != value }
    }

    fun ubyte(value: UIntRange): RuleWithDefaultRepeat<UByte> {
        return ubyte.failIf { it !in value }
    }

    fun float(value: Float, delta: Float = 0.0f): RuleWithDefaultRepeat<Float> {
        return float.failIf { it != value && abs(it - value) > delta }
    }

    fun double(value: Double, delta: Double): RuleWithDefaultRepeat<Double> {
        return double.failIf { it != value && abs(it - value) > delta }
    }

    fun float(range: ClosedRange<Float>): RuleWithDefaultRepeat<Float> {
        return float.failIf { it !in range }
    }

    fun double(range: ClosedRange<Double>): RuleWithDefaultRepeat<Double> {
        return double.failIf { it !in range }
    }

    val char = AnyCharRule()
    val digit = char('0'..'9')
    val latin = char('a'..'z', 'A'..'Z')
    val latinOrDigit = char('a'..'z', 'A'..'Z', '0'..'9')
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

    fun char(inString: String): CharPredicateRule {
        if (inString.isEmpty()) {
            throw IllegalArgumentException("Input string cannot be empty")
        }

        if (inString.length == 1) {
            return ExactCharRule(inString[0])
        }

        return CharPredicateRule(
            CharPredicateData(chars = inString.toCharArray())
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

    fun nonEmptyStr(vararg chars: Char): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(
            CharPredicates.from(*chars),
            range = 1..Int.MAX_VALUE
        )
    }

    fun nonEmptyStr(vararg range: CharRange): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(
            CharPredicates.from(*range),
            range = 1..Int.MAX_VALUE
        )
    }

    fun nonEmptyStr(chars: CharArray, ranges: Array<CharRange>): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(
            CharPredicates.from(
                ranges, chars
            ),
            range = 1..Int.MAX_VALUE
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

    fun str(string: String, ignoreCase: Boolean = false): ExactStringRule {
        return if (string.isEmpty()) {
            EmptyStringRule()
        } else {
            ExactStringRule(ignoreCase = ignoreCase, string)
        }
    }

    fun str(predicate: (Char) -> Boolean): StringCharPredicateRule {
        return StringCharPredicateRule(predicate)
    }

    val latinStr = str('A'..'Z', 'a'..'z')
    val nonEmptyLatinStr = nonEmptyStr('A'..'Z', 'a'..'z')

    val double = DoubleRule()
    val float = FloatRule()

    fun oneOf(vararg strings: CharSequence, skipper: Rule<*>? = null): RuleWithDefaultRepeat<CharSequence> {
        val withoutEmptyStrings = strings.filter {
            it.isNotEmpty()
        }

        if (withoutEmptyStrings.isEmpty()) {
            return char.failIf { true }.asString()
        }

        return if (withoutEmptyStrings.size == strings.size) {
            OneOfStringRule(withoutEmptyStrings, skipper)
        } else {
            OptionalRule(OneOfStringRule(withoutEmptyStrings.shuffled(), skipper))
        }
    }

    fun oneOf(strings: Collection<CharSequence>, skipper: Rule<*>? = null): RuleWithDefaultRepeat<CharSequence> {
        val withoutEmptyStrings = strings.filter {
            it.isNotEmpty()
        }

        if (withoutEmptyStrings.isEmpty()) {
            return char.failIf { true }.asString()
        }

        return if (withoutEmptyStrings.size == strings.size) {
            OneOfStringRule(withoutEmptyStrings, skipper)
        } else {
            OptionalRule(OneOfStringRule(withoutEmptyStrings.shuffled(), skipper))
        }
    }

    fun caseInsensitiveOneOf(vararg strings: CharSequence): RuleWithDefaultRepeat<CharSequence> {
        val withoutEmptyStrings = strings.filter {
            it.isNotEmpty()
        }

        if (withoutEmptyStrings.isEmpty()) {
            return char.failIf { true }.asString()
        }

        return if (withoutEmptyStrings.size == strings.size) {
            OneOfStringRuleCaseInsensetive(withoutEmptyStrings)
        } else {
            OptionalRule(OneOfStringRuleCaseInsensetive(withoutEmptyStrings.shuffled()))
        }
    }

    fun caseInsensitiveOneOf(strings: Collection<CharSequence>, skipper: Rule<*>? = null): RuleWithDefaultRepeat<CharSequence> {
        val withoutEmptyStrings = strings.filter {
            it.isNotEmpty()
        }

        if (withoutEmptyStrings.isEmpty()) {
            return char.failIf { true }.asString()
        }

        return if (withoutEmptyStrings.size == strings.size) {
            OneOfStringRuleCaseInsensetive(withoutEmptyStrings, skipper)
        } else {
            OptionalRule(OneOfStringRuleCaseInsensetive(withoutEmptyStrings.shuffled(), skipper))
        }
    }

    fun dynamicString(stringProvider: () -> CharSequence): DynamicStringRule {
        return DynamicStringRule(stringProvider)
    }

    fun <T : Any> dynamicRule(ruleFactory: () -> Rule<T>): DynamicRule<T> {
        return DynamicRule(name = null, ruleFactory = ruleFactory)
    }

    val end = EndRule()
    val start = StartRule()
    val boolean = BooleanRule()

    fun regexp(pattern: String): RegexpRule {
        return RegexpRule(Regex(pattern))
    }

    fun regexp(pattern: Regex): RegexpRule {
        return RegexpRule(pattern)
    }

    fun <T : Any> group(resultSequenceRule: ResultSequenceRule<T>): GroupSequenceRule<T> {
        return GroupSequenceRule<T>(rule = resultSequenceRule)
    }

    val jsonObject = JsonObjectRule()
    val jsonArray = JsonArrayRule()

    fun <To : Any> jsonObject(mapper: (CharSequence) -> To): TransformRule<CharSequence, To> {
        return jsonObject.asString().map(mapper)
    }

    fun <To : Any> jsonArray(mapper: (CharSequence) -> To): TransformRule<CharSequence, To> {
        return jsonArray.asString().map(mapper)
    }

    fun skipUntil(rule: Rule<*>): Rule<*> {
        return (char - rule).repeat() + rule
    }

    fun <T : Any> moveAfterFirstMatchOf(rule: Rule<T>): AfterFirstMatchOfRule<T> {
        return AfterFirstMatchOfRule(rule)
    }

    fun <T: Any, Data> grammar(
        dataFactory: () -> Data,
        defineRule: (data: Data) -> Rule<*>,
        getResult: (data: Data) -> T
    ): GrammarRule<T, Data> {
        return GrammarRule(
            dataFactory = dataFactory,
            defineRule = defineRule,
            getResult = getResult,
            name = null
        )
    }
}

fun Regex.toRule(): RegexpRule {
    Rules.grammar(
        dataFactory = { this },
        defineRule = { regex -> RegexpRule(regex) },
        getResult = { regex -> regex.pattern }
    )
    return Rules.regexp(this)
}