package com.example.kotlinspirit

private abstract class BaseIntRuleIterator : BaseParseIterator<Int>() {
    protected var value: Int = -1

    override fun prev() {
        super.prev()
        value -= getChar() - '0'
        value /= 10
    }

    override fun resetSeek(seek: Int) {
        super.resetSeek(seek)
        value = -1
    }
}

private class IntRuleIterator : BaseIntRuleIterator() {
    private var sign = 1

    override fun next(): Int {
        if (isEof()) {
            return if (value < 0) {
                StepCode.EOF
            } else {
                StepCode.COMPLETE
            }
        }

        val char = readChar()
        return when {
            char == '-' -> {
                if (seek != seekBegin + 1) {
                    StepCode.INVALID_NUMBER
                } else {
                    sign = -1
                    StepCode.HAS_NEXT
                }
            }
            char == '0' && value < 0 -> {
                if (isEof() || !getChar().isDigit()) {
                    value = 0
                    StepCode.COMPLETE
                } else {
                    StepCode.NUMBER_STARTED_FROM_ZERO
                }
            }
            char.isDigit() -> {
                if (value < 0) {
                    value = 0
                }

                value *= 10
                value += char - '0'
                StepCode.HAS_NEXT_MAY_COMPLETE
            }
            else -> {
                return if (value < 0) {
                    StepCode.INVALID_NUMBER
                } else {
                    seek--
                    StepCode.COMPLETE
                }
            }
        }
    }

    override fun resetSeek(seek: Int) {
        super.resetSeek(seek)
        sign = 1
    }

    override fun getResult(): Int {
        return value * sign
    }
}

private class IntRangeRuleIterator(
    private val range: IntRange
) : BaseIntRuleIterator()
{
    private var sign = 1

    override fun next(): Int {
        if (isEof()) {
            return if (value < 0) {
                StepCode.EOF
            } else {
                StepCode.COMPLETE
            }
        }

        val char = readChar()
        return when {
            char == '-' -> {
                if (seek != seekBegin + 1) {
                    StepCode.INVALID_NUMBER
                } else {
                    sign = -1
                    StepCode.HAS_NEXT
                }
            }
            char == '0' && value < 0 -> {
                if (isEof() || !getChar().isDigit()) {
                    value = 0
                    StepCode.COMPLETE
                } else {
                    StepCode.NUMBER_STARTED_FROM_ZERO
                }
            }
            char.isDigit() -> {
                if (value < 0) {
                    value = 0
                }

                value *= 10
                value += char - '0'
                if (range.contains(value * sign)) {
                    StepCode.HAS_NEXT
                } else {
                    StepCode.INT_NOT_IN_REQUESTED_RANGE
                }
            }
            else -> {
                return if (value < 0) {
                    StepCode.INVALID_NUMBER
                } else {
                    seek--
                    StepCode.COMPLETE
                }
            }
        }
    }

    override fun resetSeek(seek: Int) {
        super.resetSeek(seek)
        sign = 1
    }

    override fun getResult(): Int {
        return value * sign
    }
}

private class UnsignedIntRuleIterator
    : BaseIntRuleIterator()
{
    override fun next(): Int {
        if (isEof()) {
            return if (value < 0) {
                StepCode.EOF
            } else {
                StepCode.COMPLETE
            }
        }

        val char = readChar()
        return when {
            char == '0' && value < 0 -> {
                if (isEof() || !getChar().isDigit()) {
                    value = 0
                    StepCode.COMPLETE
                } else {
                    StepCode.NUMBER_STARTED_FROM_ZERO
                }
            }
            char.isDigit() -> {
                if (value < 0) {
                    value = 0
                }

                value *= 10
                value += char - '0'
                StepCode.HAS_NEXT
            }
            else -> {
                return if (value < 0) {
                    StepCode.INVALID_NUMBER
                } else {
                    seek--
                    StepCode.COMPLETE
                }
            }
        }
    }

    override fun getResult(): Int {
        return value
    }
}

private class UnsignedIntRangeRuleIterator(
    private val range: IntRange
)
    : BaseParseIterator<Int>()
{
    private var value: Int = -1

    override fun next(): Int {
        if (isEof()) {
            return if (value < 0) {
                StepCode.EOF
            } else {
                StepCode.COMPLETE
            }
        }

        val char = readChar()
        return when {
            char == '-' -> {
                if (seek != seekBegin) {
                    StepCode.INVALID_NUMBER
                } else {
                    StepCode.HAS_NEXT
                }
            }
            char == '0' && value < 0 -> {
                if (isEof() || !getChar().isDigit()) {
                    value = 0
                    StepCode.COMPLETE
                } else {
                    StepCode.NUMBER_STARTED_FROM_ZERO
                }
            }
            char.isDigit() -> {
                if (value < 0) {
                    value = 0
                }

                value *= 10
                value += char - '0'
                if (range.contains(value)) {
                    StepCode.HAS_NEXT
                } else {
                    StepCode.INT_NOT_IN_REQUESTED_RANGE
                }
            }
            else -> {
                return if (value < 0) {
                    StepCode.INVALID_NUMBER
                } else {
                    seek--
                    StepCode.COMPLETE
                }
            }
        }
    }

    override fun getResult(): Int {
        return value
    }
}

internal class IntRule : BaseRule<Int>() {
    override fun createParseIterator(): ParseIterator<Int> {
        return IntRuleIterator()
    }
}

internal class UnsignedIntRule : BaseRule<Int>() {
    override fun createParseIterator(): ParseIterator<Int> {
        return UnsignedIntRuleIterator()
    }
}

internal class IntRangeRule(
    private val range: IntRange
): BaseRule<Int>() {
    override fun createParseIterator(): ParseIterator<Int> {
        return IntRangeRuleIterator(range)
    }
}

internal class UnsignedIntRangeRule(
    private val range: IntRange
): BaseRule<Int>() {
    override fun createParseIterator(): ParseIterator<Int> {
        return UnsignedIntRangeRuleIterator(range)
    }
}