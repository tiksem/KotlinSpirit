package com.example.kotlinspirit

private abstract class BaseIntRuleIterator : BaseParseIterator<Int>() {
    protected var value: Int = -1

    override fun prev(context: ParseContext) {
        super.prev(context)
        value -= context.getChar() - '0'
        value /= 10
    }

    override fun resetSeek(seek: Int) {
        super.resetSeek(seek)
        value = -1
    }
}

private class IntRuleIterator : BaseIntRuleIterator() {
    private var sign = 1

    override fun next(context: ParseContext): Int {
        if (isEof(context)) {
            return if (value < 0) {
                StepCode.EOF
            } else {
                StepCode.COMPLETE
            }
        }

        val char = context.readChar()
        return when {
            char == '-' -> {
                if (seek == seekBegin + 1) {
                    sign = -1
                    StepCode.HAS_NEXT
                } else {
                    StepCode.COMPLETE
                }
            }
            char == '0' && value < 0 -> {
                if (isEof(context) || !context.getChar().isDigit()) {
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

                var value = this.value
                value *= 10
                value += char - '0'
                if (value < this.value) {
                    StepCode.INT_OUT_OF_RANGE
                } else {
                    this.value = value
                    StepCode.HAS_NEXT_MAY_COMPLETE
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

    override fun getResult(context: ParseContext): Int {
        return value * sign
    }
}

private class IntRangeRuleIterator(
    private val range: IntRange
) : BaseIntRuleIterator()
{
    private var sign = 1

    override fun next(context: ParseContext): Int {
        if (isEof(context)) {
            return if (value < 0) {
                StepCode.EOF
            } else {
                StepCode.COMPLETE
            }
        }

        val char = context.readChar()
        return when {
            char == '-' -> {
                if (value < 0) {
                    sign = -1
                    StepCode.HAS_NEXT
                } else {
                    StepCode.COMPLETE
                }
            }
            char == '0' && value < 0 -> {
                if (isEof(context) || !context.getChar().isDigit()) {
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

                var value = this.value
                value *= 10
                value += char - '0'
                when {
                    value < this.value -> {
                        StepCode.INT_OUT_OF_RANGE
                    }
                    value * sign in range -> {
                        this.value = value
                        StepCode.HAS_NEXT
                    }
                    else -> {
                        StepCode.INT_NOT_IN_REQUESTED_RANGE
                    }
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

    override fun getResult(context: ParseContext): Int {
        return value * sign
    }
}

private class UnsignedIntRuleIterator
    : BaseIntRuleIterator()
{
    override fun next(context: ParseContext): Int {
        if (isEof(context)) {
            return if (value < 0) {
                StepCode.EOF
            } else {
                StepCode.COMPLETE
            }
        }

        val char = context.readChar()
        return when {
            char == '0' && value < 0 -> {
                if (isEof(context) || !context.getChar().isDigit()) {
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

                var value = this.value
                value *= 10
                value += char - '0'
                if (value < this.value) {
                    StepCode.INT_OUT_OF_RANGE
                } else {
                    this.value = value
                    StepCode.HAS_NEXT
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

    override fun getResult(context: ParseContext): Int {
        return value
    }
}

private class UnsignedIntRangeRuleIterator(
    private val range: IntRange
)
    : BaseParseIterator<Int>()
{
    private var value: Int = -1

    override fun next(context: ParseContext): Int {
        if (isEof(context)) {
            return if (value < 0) {
                StepCode.EOF
            } else {
                StepCode.COMPLETE
            }
        }

        val char = context.readChar()
        return when {
            char == '-' -> {
                if (seek != seekBegin) {
                    StepCode.INVALID_NUMBER
                } else {
                    StepCode.HAS_NEXT
                }
            }
            char == '0' && value < 0 -> {
                if (isEof(context) || !context.getChar().isDigit()) {
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

                var value = this.value
                value *= 10
                value += char - '0'
                when {
                    value < this.value -> {
                        StepCode.INT_OUT_OF_RANGE
                    }
                    value in range -> {
                        this.value = value
                        StepCode.HAS_NEXT
                    }
                    else -> {
                        StepCode.INT_NOT_IN_REQUESTED_RANGE
                    }
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

    override fun getResult(context: ParseContext): Int {
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