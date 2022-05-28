package com.example.kotlinspirit

object Rules {
    val int: Rule<Int> = IntRule()
    val uint: Rule<Int> = UnsignedIntRule()

    fun int(range: IntRange): Rule<Int> {
        return if (range.first < 0 || range.last < 0) {
            IntRangeRule(range)
        } else {
            UnsignedIntRangeRule(range)
        }
    }

    fun int(value: Int): Rule<Int> {
        return ExactIntRule(value)
    }

    val char: Rule<Char> = AnyCharRule()

    fun char(vararg chars: Char): CharRule {
        assert(chars.isNotEmpty())
        return CharMatchRule(CharPredicates.from(*chars))
    }

    fun char(vararg ranges: CharRange): CharRule {
        assert(ranges.isNotEmpty())
        return CharMatchRule(CharPredicates.from(*ranges))
    }

    fun char(
        ranges: Array<CharRange>,
        chars: CharArray
    ) : CharRule {
        assert(ranges.isNotEmpty() || chars.isNotEmpty())
        return CharMatchRule(
            CharPredicates.from(ranges, chars)
        )
    }

    fun char(predicate: (Char) -> Boolean) : CharRule {
        return CharMatchRule(predicate)
    }

    fun oneOf(vararg strings: String): OneOfStringRule {
        return OneOfStringRule(strings.toList())
    }

    fun oneOf(strings: List<String>): OneOfStringRule {
        return OneOfStringRule(strings)
    }

    val str = AnyStringRule()

    fun str(string: CharSequence): StringRule {
        return ExactStringRule(string)
    }

    fun str(vararg chars: Char): StringRule {
        assert(chars.isNotEmpty())
        return MatchStringRule(CharPredicates.from(*chars))
    }

    fun str(vararg ranges: CharRange): StringRule {
        assert(ranges.isNotEmpty())
        return MatchStringRule(CharPredicates.from(*ranges))
    }

    fun str(
        ranges: Array<CharRange>,
        chars: CharArray
    ) : StringRule {
        assert(ranges.isNotEmpty() || chars.isNotEmpty())
        return MatchStringRule(
            CharPredicates.from(ranges, chars)
        )
    }

    fun str(predicate: (Char) -> Boolean) : StringRule {
        return MatchStringRule(predicate)
    }

    private val quote by lazy {
        char('"', '\'')
    }

    fun quotedString(callback: (CharSequence) -> Unit): Rule<CharSequence> {
        return quote + str {
            it != '"' && it != '\''
        }.on(success = callback) + quote
    }

    fun quotedString(): Rule<CharSequence> {
        return quote + str {
            it != '"' && it != '\''
        } + quote
    }

    fun quotedString(quote: Rule<*>, callback: (CharSequence) -> Unit): Rule<CharSequence> {
        return quote + (str - quote).on(success = callback) + quote
    }

    fun quotedString(quote: Rule<*>): Rule<CharSequence> {
        return quote + (str - quote) + quote
    }
}