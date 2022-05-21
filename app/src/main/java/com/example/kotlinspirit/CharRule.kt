package com.example.kotlinspirit

import java.lang.IllegalStateException
import java.util.*

private const val DOES_NOT_MATCH = "required char was not found"

abstract class CharRule : Rule<Char> {
}

internal open class AnyCharRule : CharRule() {
    override fun parse(state: ParseState, requireResult: Boolean) {
        if (state.checkEof()) {
            return
        }

        state.startParseToken()
        state.seek++
    }

    override fun getResult(array: CharArray, seekBegin: Int, seekEnd: Int): Char {
        return array[seekBegin]
    }
}

internal abstract class CharMatch : CharRule() {
    abstract fun isValid(char: Char): Boolean

    override fun parse(state: ParseState, requireResult: Boolean) {
        if (state.checkEof()) {
            return
        }

        state.startParseToken()
        if (!isValid(state.readChar())) {
            state.errorReason = DOES_NOT_MATCH
        }
    }

    override fun getResult(array: CharArray, seekBegin: Int, seekEnd: Int): Char {
        return array[seekBegin]
    }
}

internal class ExactCharRule(
    private val char: Char
) : CharMatch() {
    override fun isValid(char: Char): Boolean {
        return this.char == char
    }
}

internal class OneOfCharRule(
    private val chars: CharArray
) : CharMatch() {
    override fun isValid(char: Char): Boolean {
        return Arrays.binarySearch(chars, char) >= 0
    }
}

internal open class RangeCharRule(
    private val range: CharRange
): CharMatch() {
    override fun isValid(char: Char): Boolean {
        return range.contains(char)
    }
}

internal open class RangesCharRule(
    private val ranges: Array<CharRange>
): CharMatch() {
    override fun isValid(char: Char): Boolean {
        return ranges.find {
            it.contains(char)
        } != null
    }
}

internal class RangesAndCharMatchCharRule(
    private val char: Char,
    ranges: Array<CharRange>
) : RangesCharRule(ranges) {
    override fun isValid(char: Char): Boolean {
        return this.char == char || super.isValid(char)
    }
}

internal class RangesAndCharsMatchCharRule(
    private val chars: CharArray,
    ranges: Array<CharRange>
) : RangesCharRule(ranges) {
    override fun isValid(char: Char): Boolean {
        return super.isValid(char) || Arrays.binarySearch(chars, char) >= 0
    }
}

internal class RangeAndCharsMatchCharRule(
    private val chars: CharArray,
    range: CharRange
) : RangeCharRule(range) {
    override fun isValid(char: Char): Boolean {
        return super.isValid(char) || Arrays.binarySearch(chars, char) >= 0
    }
}

internal class RangeAndCharMatchCharRule(
    private val char: Char,
    private val range: CharRange
) : CharMatch() {
    override fun isValid(char: Char): Boolean {
        return this.char == char || range.contains(char)
    }
}

val char: Rule<Char> = AnyCharRule()

fun char(vararg chars: Char): Rule<Char> {
    assert(chars.isNotEmpty())
    return if (chars.size == 1) {
        ExactCharRule(chars[0])
    } else {
        OneOfCharRule(chars.sortedArray())
    }
}

fun char(vararg ranges: CharRange): Rule<Char> {
    assert(ranges.isNotEmpty())
    return if (ranges.size == 1) {
        RangeCharRule(ranges[0])
    } else {
        RangesCharRule(ranges as Array<CharRange>)
    }
}

fun char(
    ranges: Array<CharRange>,
    chars: CharArray
) : Rule<Char> {
    assert(ranges.isNotEmpty() || chars.isNotEmpty())
    return when {
        ranges.isEmpty() -> char(*chars)
        chars.isEmpty() -> char(*ranges)
        ranges.size == 1 && chars.size == 1 -> RangeAndCharMatchCharRule(
            chars[0], ranges[0]
        )
        ranges.size > 1 && chars.size > 1 -> RangesAndCharsMatchCharRule(
            chars.sortedArray(), ranges
        )
        ranges.size > 1 && chars.size == 1 -> {
            RangesAndCharMatchCharRule(
                chars[0], ranges
            )
        }
        ranges.size == 1 && chars.size > 1 -> {
            RangeAndCharsMatchCharRule(
                chars.sortedArray(), ranges[0]
            )
        }
        else -> throw IllegalStateException("Unexpected behaviour")
    }
}