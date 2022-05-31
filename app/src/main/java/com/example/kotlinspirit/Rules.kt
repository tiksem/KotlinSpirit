package com.example.kotlinspirit

object Rules {
    val int: Rule<Int>
        get() = IntRule()

    val uint: Rule<Int>
        get() = UnsignedIntRule()

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

    val char: Rule<Char> get() = AnyCharRule()

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

    val spaceChar: CharRule
        get() = char {
            it.isWhitespace()
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

    val str get() = AnyStringRule()

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

    val spaceStr: StringRule
        get() = str {
            it.isWhitespace()
        }

    val latinStr: StringRule
        get() = str('a'..'z', 'A'..'Z')

    fun quotedString(callback: (CharSequence) -> Unit): Rule<CharSequence> {
        var quote = '\"'
        return char('"', '\'').on {
            quote = it
        } + str {
            it != '"' && it != '\''
        }.on {
            callback(it)
        } + char { it == quote }
    }

    fun quotedString(): Rule<CharSequence> {
        var quote = '\"'
        return char('"', '\'').on {
            quote = it
        } + str {
            it != '"' && it != '\''
        } + char { it == quote }
    }

    fun quotedString(quote: Rule<*>, callback: (CharSequence) -> Unit): Rule<CharSequence> {
        return quote + (str - quote).on(success = callback) + quote
    }

    fun quotedString(quote: Rule<*>): Rule<CharSequence> {
        return quote + (str - quote) + quote
    }
}