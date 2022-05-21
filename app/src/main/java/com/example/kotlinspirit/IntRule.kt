package com.example.kotlinspirit

private const val NUMBER_STARTED_WITH_ZERO = "number started with zero"
private const val INVALID_NUMBER = "invalid number"
private const val NOT_IN_RANGE = "int not in range"
private const val DOES_NOT_MATCH = "int does not match"

private open class IntRule : Rule<Int> {
    private var result: Int = 0

    override fun parse(state: ParseState) {
        state.startParseToken()
        result = 0

        if (state.checkEof()) {
            return
        }

        val minus = state.getChar() == '-'
        if (minus) {
            state.seek++
            if (state.checkEof()) {
                return
            }
        }

        val firstDigit = state.readChar()
        if (firstDigit.isDigit()) {
            result = firstDigit.digitToInt()
            if (result == 0) {
                if (state.isEof() || !state.getChar().isDigit()) {
                    result = 0
                    return
                } else {
                    state.errorReason = NUMBER_STARTED_WITH_ZERO
                    return
                }
            } else {
                while (state.seek < state.array.size) {
                    val digit = state.readChar()
                    if (!digit.isDigit()) {
                        state.seek--
                        break
                    } else {
                        result *= 10
                        result += digit - '0'
                    }
                }
            }
        } else {
            state.errorReason = INVALID_NUMBER
            return
        }

        if (minus) {
            result = -result
        }
    }

    override fun getResult(state: ParseState): Int {
        return result
    }
}

private class IntRangeRule(
    val a: Int,
    val b: Int
): IntRule() {
    override fun parse(state: ParseState) {
        super.parse(state)

        if (!state.hasError) {
            val result = getResult(state)
            if (result !in a..b) {
                state.errorReason = NOT_IN_RANGE
            }
        }
    }
}

val int: Rule<Int> = IntRule()

fun int(value: Int): Rule<Int> {
    return object : Rule<Int> {
        val rule = ExactStringRule(value.toString())

        override fun parse(state: ParseState) {
            rule.parse(state)
            if (!state.hasError) {
                if (rule.getResult(state).toIntOrNull() != value) {
                    state.errorReason = DOES_NOT_MATCH
                }
            }
        }

        override fun getResult(state: ParseState): Int {
            return value
        }
    }
}

fun int(a: Int, b: Int): Rule<Int> {
    return IntRangeRule(a, b)
}