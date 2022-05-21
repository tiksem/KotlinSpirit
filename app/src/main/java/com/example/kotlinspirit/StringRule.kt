package com.example.kotlinspirit

import java.lang.IllegalStateException
import java.util.*

class CharsSetStringRule internal constructor(
    minimumLength: Int,
    maximumLength: Int,
    private val chars: CharArray,
) : MatchStringRule(minimumLength, maximumLength) {
    override operator fun get(range: IntRange): MatchStringRule {
        return CharsSetStringRule(
            minimumLength = range.first,
            maximumLength = range.last,
            chars = this.chars
        )
    }

    override fun isValidChar(char: Char): Boolean {
        return Arrays.binarySearch(chars, char) >= 0
    }
}

class CharStringRule internal constructor(
    minimumLength: Int,
    maximumLength: Int,
    private val char: Char
) : MatchStringRule(minimumLength, maximumLength) {
    override operator fun get(range: IntRange): MatchStringRule {
        return CharStringRule(
            minimumLength = range.first,
            maximumLength = range.last,
            char = this.char
        )
    }

    override fun isValidChar(char: Char): Boolean {
        return this.char == char
    }
}

class CharRangeStringRule internal constructor(
    minimumLength: Int,
    maximumLength: Int,
    private val range: CharRange
) : MatchStringRule(minimumLength, maximumLength)  {
    override operator fun get(range: IntRange): MatchStringRule {
        return CharRangeStringRule(
            minimumLength = range.first,
            maximumLength = range.last,
            range = this.range
        )
    }

    override fun isValidChar(char: Char): Boolean {
        return range.contains(char)
    }
}

class CharRangesStringRule internal constructor(
    minimumLength: Int,
    maximumLength: Int,
    private val ranges: Array<out CharRange>
) : MatchStringRule(minimumLength, maximumLength) {
    override operator fun get(range: IntRange): MatchStringRule {
        return CharRangesStringRule(
            minimumLength = range.first,
            maximumLength = range.last,
            ranges = this.ranges
        )
    }

    override fun isValidChar(char: Char): Boolean {
        return ranges.find {
            it.contains(char)
        } != null
    }
}

class SingleCharAndSingleRangeStringRule internal constructor(
    minimumLength: Int,
    maximumLength: Int,
    private val char: Char,
    private val range: CharRange
) : MatchStringRule(minimumLength, maximumLength) {
    override fun isValidChar(char: Char): Boolean {
        return this.char == char || range.contains(char)
    }

    override fun get(range: IntRange): MatchStringRule {
        return SingleCharAndSingleRangeStringRule(
            minimumLength = range.first,
            maximumLength = range.last,
            char = char,
            range = this.range
        )
    }
}

class SingleCharAndRangesStringRule internal constructor(
    minimumLength: Int,
    maximumLength: Int,
    private val char: Char,
    private val ranges: Array<CharRange>
) : MatchStringRule(minimumLength, maximumLength) {
    override fun isValidChar(char: Char): Boolean {
        return this.char == char || ranges.find {
            it.contains(char)
        } != null
    }

    override fun get(range: IntRange): MatchStringRule {
        return SingleCharAndRangesStringRule(
            minimumLength = range.first,
            maximumLength = range.last,
            char = char,
            ranges = this.ranges
        )
    }
}

class CharsAndRangeStringRule internal constructor(
    minimumLength: Int,
    maximumLength: Int,
    private val chars: CharArray,
    private val range: CharRange
) : MatchStringRule(minimumLength, maximumLength) {
    override fun isValidChar(char: Char): Boolean {
        return range.contains(char) || Arrays.binarySearch(chars, char) >= 0
    }

    override fun get(range: IntRange): MatchStringRule {
        return CharsAndRangeStringRule(
            minimumLength = range.first,
            maximumLength = range.last,
            chars = chars,
            range = this.range
        )
    }
}

class CharsAndRangesStringRule internal constructor(
    minimumLength: Int,
    maximumLength: Int,
    private val chars: CharArray,
    private val ranges: Array<CharRange>
) : MatchStringRule(minimumLength, maximumLength) {
    override fun isValidChar(char: Char): Boolean {
        return Arrays.binarySearch(chars, char) >= 0 || ranges.find {
            it.contains(char)
        } != null
    }

    override fun get(range: IntRange): MatchStringRule {
        return CharsAndRangesStringRule(
            minimumLength = range.first,
            maximumLength = range.last,
            chars = chars,
            ranges = this.ranges
        )
    }
}

fun str(vararg chars: Char): MatchStringRule {
    assert(chars.isNotEmpty())
    return if (chars.size == 1) {
        CharStringRule(1, Int.MAX_VALUE, chars[0])
    } else {
        CharsSetStringRule(1, Int.MAX_VALUE, chars.sortedArray())
    }
}

fun str(vararg charRange: CharRange): MatchStringRule {
    return if (charRange.size == 1) {
        CharRangeStringRule(1, Int.MAX_VALUE, charRange[0])
    } else {
        CharRangesStringRule(1, Int.MAX_VALUE, charRange)
    }
}

fun str(
    ranges: Array<CharRange>,
    chars: CharArray
) : MatchStringRule {
    assert(ranges.isNotEmpty() || chars.isNotEmpty())
    return when {
        ranges.isEmpty() -> str(*chars)
        chars.isEmpty() -> str(*ranges)
        ranges.size == 1 && chars.size == 1 -> SingleCharAndSingleRangeStringRule(
            1, Int.MAX_VALUE, chars[0], ranges[0]
        )
        ranges.size > 1 && chars.size > 1 -> CharsAndRangesStringRule(
            1, Int.MAX_VALUE, chars.sortedArray(), ranges
        )
        ranges.size > 1 && chars.size == 1 -> {
            SingleCharAndRangesStringRule(
                1, Int.MAX_VALUE, chars[0], ranges
            )
        }
        ranges.size == 1 && chars.size > 1 -> {
            CharsAndRangeStringRule(
                1, Int.MAX_VALUE, chars.sortedArray(), ranges[0]
            )
        }
        else -> throw IllegalStateException("Unexpected behaviour")
    }
}