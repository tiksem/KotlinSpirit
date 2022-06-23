package com.example.kotlinspirit

import java.lang.IllegalStateException

private inline fun getPowerOf10(exp: Int): Double {
    return if (exp > POWERS_OF_10.size) {
        Double.POSITIVE_INFINITY
    } else {
        POWERS_OF_10[exp]
    }
}

private const val STATE_CHECK_INTEGER_SIGN = 0
private const val STATE_INTEGER_SIGN_CHECKED = 1
private const val STATE_DOT_CHECKED_AFTER_SIGN = 2
private const val STATE_DOT_CHECKED = 3
private const val STATE_DOT_CHECKED_AFTER_INTEGER = 4
private const val STATE_INTEGER = 5
private const val STATE_FRACTION = 6
private const val STATE_EXP_CHECK_SIGN = 7
private const val STATE_EXP_CHECK_SIGN_AFTER_DOT = 8
private const val STATE_EXP_SIGN_CHECKED = 9
private const val STATE_EXP_SIGN_CHECKED_AFTER_DOT = 10
private const val STATE_EXP_VALUE = 11

private val seekRollBackMap = intArrayOf(0, 1, 2, 1, 1, 0, 0, 1, 2, 2, 3, 0)
private val canCompleteStep = booleanArrayOf(
    false, false, false, false, true, true, true, true, true, true, true, true
)

private const val NO_STEP_CHECK_SIGN_OR_DOT_STATE = 0
private const val NO_STEP_SIGN_CHECKED = 1
private const val NO_STEP_DOT_CHECKED = 2
private const val NO_STEP_DOT_CHECKED_AFTER_SIGN = 3

class DoubleRule : RuleWithDefaultRepeat<Double>() {
    private var state = STATE_CHECK_INTEGER_SIGN
    private var sign = 1.0
    private var expMinus = false
    private var fractionE = 10.0
    private var integer = 0.0
    private var fraction = 0.0
    private var exp = 0

    private var noStepSuccessFlag = false

    override fun parse(seek: Int, string: CharSequence): Long {
        val length = string.length
        if (seek >= length) {
            return createStepResult(
                seek = seek,
                stepCode = StepCode.EOF
            )
        }

        // Skip integer part
        var i = seek
        val c = string[i]
        var noMoreDots = false
        if (c == '-' || c == '+' || c == '.') {
            i++
            if (i >= length) {
                return createStepResult(
                    seek = seek,
                    stepCode = StepCode.INVALID_DOUBLE
                )
            }

            noMoreDots = c == '.'
            if (!noMoreDots && string[i] == '.') {
                if (++i >= length) {
                    return createStepResult(
                        seek = seek,
                        stepCode = StepCode.INVALID_DOUBLE
                    )
                }
                noMoreDots = true
            }

            if (string[i].isDigit()) {
                i++
                while (i < length && string[i].isDigit()) {
                    i++
                }
            } else {
                return createStepResult(
                    seek = seek,
                    stepCode = StepCode.INVALID_DOUBLE
                )
            }
        } else if(c.isDigit()) {
            i++
            while (i < length && string[i].isDigit()) {
                i++
            }
        } else {
            return createStepResult(
                seek = seek,
                stepCode = StepCode.INVALID_DOUBLE
            )
        }

        if (i >= length) {
            return createComplete(i)
        }

        when (string[i]) {
            '.' -> {
                if (noMoreDots) {
                    return if (i == seek) {
                        createStepResult(
                            seek = seek,
                            stepCode = StepCode.INVALID_DOUBLE
                        )
                    } else {
                        createComplete(i)
                    }
                }

                i++
                while (i < length && string[i].isDigit()) {
                    i++
                }
                val saveI = i
                if (i < length) {
                    val v = string[i++]
                    if (v == 'e' || v == 'E') {
                        if (i < length) {
                            val c2 = string[i]
                            if (c2 == '-' || c2 == '+') {
                                ++i
                                return if (i < length && string[i].isDigit()) {
                                    i++
                                    while (i < length && string[i].isDigit()) {
                                        i++
                                    }
                                    createComplete(i)
                                } else {
                                    createComplete(i - 2)
                                }
                            } else {
                                return if (string[i].isDigit()) {
                                    i++
                                    while (i < length && string[i].isDigit()) {
                                        i++
                                    }
                                    createComplete(i)
                                } else {
                                    createComplete(i - 1)
                                }
                            }
                        } else {
                            return createComplete(saveI)
                        }
                    }
                } else {
                    return createComplete(saveI)
                }
            }
            'e', 'E' -> {
                val saveI = i++
                if (i < length) {
                    if (string[i] == '-') {
                        ++i
                        return if (i < length && string[i].isDigit()) {
                            i++
                            while (i < length && string[i].isDigit()) {
                                i++
                            }
                            createComplete(i)
                        } else {
                            createComplete(i - 2)
                        }
                    } else {
                        return if (string[i].isDigit()) {
                            i++
                            while (i < length && string[i].isDigit()) {
                                i++
                            }
                            createComplete(i)
                        } else {
                            createComplete(i - 1)
                        }
                    }
                } else {
                    return createComplete(saveI)
                }
            }
            else -> {
                return createComplete(i)
            }
        }

        return createComplete(i)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Double>) {
        val length = string.length
        if (seek >= length) {
            result.stepResult = createStepResult(
                seek = seek,
                stepCode = StepCode.EOF
            )
            return
        }

        // Skip integer part
        var i = seek
        var c = string[i]
        var noMoreDots = false
        var integerPart = 0.0
        var fractionPart = 0.0
        val minus = c == '-'
        if (minus || c == '+' || c == '.') {
            i++
            if (i >= length) {
                result.stepResult = createStepResult(
                    seek = seek,
                    stepCode = StepCode.INVALID_DOUBLE
                )
                return
            }

            noMoreDots = c == '.'
            if (!noMoreDots && string[i] == '.') {
                if (++i >= length) {
                    result.stepResult = createStepResult(
                        seek = seek,
                        stepCode = StepCode.INVALID_DOUBLE
                    )
                    return
                }
                noMoreDots = true
            }

            c = string[i]
            if (noMoreDots) {
                var e = 10.0
                if (c.isDigit()) {
                    fractionPart = (c - '0').toDouble() / e
                    i++
                    while (i < length) {
                        c = string[i]
                        if (c.isDigit()) {
                            e *= 10
                            fractionPart += (c - '0').toDouble() / e
                            i++
                        } else {
                            break
                        }
                    }
                } else {
                    result.stepResult = createStepResult(
                        seek = seek,
                        stepCode = StepCode.INVALID_DOUBLE
                    )
                    return
                }
            } else {
                if (c.isDigit()) {
                    integerPart = (c - '0').toDouble()
                    i++
                    while (i < length) {
                        c = string[i]
                        if (c.isDigit()) {
                            integerPart *= 10
                            integerPart += c - '0'
                            i++
                        } else {
                            break
                        }
                    }
                } else {
                    result.stepResult = createStepResult(
                        seek = seek,
                        stepCode = StepCode.INVALID_DOUBLE
                    )
                    return
                }
            }
        } else if(c.isDigit()) {
            integerPart = (c - '0').toDouble()
            i++
            while (i < length) {
                c = string[i]
                if (c.isDigit()) {
                    integerPart *= 10
                    integerPart += c - '0'
                    i++
                } else {
                    break
                }
            }
        } else {
            result.stepResult = createStepResult(
                seek = seek,
                stepCode = StepCode.INVALID_DOUBLE
            )
            return
        }

        if (i >= length) {
            result.stepResult = createComplete(i)
            result.data = if (minus) {
                -integerPart - fractionPart
            } else {
                integerPart + fractionPart
            }
            return
        }

        var saveI = i
        when (string[i]) {
            '.' -> {
                if (noMoreDots || ++i >= length) {
                    result.stepResult = createComplete(i)
                    result.data = if (minus) {
                        -integerPart - fractionPart
                    } else {
                        integerPart + fractionPart
                    }
                    return
                }

                c = string[i]
                var e = 10.0
                if (c.isDigit()) {
                    fractionPart = (c - '0').toDouble() / e
                    i++
                    while (i < length) {
                        c = string[i]
                        if (c.isDigit()) {
                            e *= 10
                            fractionPart += (c - '0').toDouble() / e
                            i++
                        } else {
                            break
                        }
                    }
                } else if (c != 'e' && c != 'E') {
                    result.stepResult = createComplete(saveI)
                    result.data = if (minus) {
                        -integerPart
                    } else {
                        integerPart
                    }
                    return
                }

                if (i < length) {
                    val v = string[i++]
                    if (v == 'e' || v == 'E') {
                        var exp = 0
                        if (i < length) {
                            c = string[i]
                            if (c == '-' || c == '+') {
                                ++i
                                val expMinus = c == '-'
                                if (i < length) {
                                    c = string[i]
                                    if (c.isDigit()) {
                                        i++
                                        exp = c - '0'
                                        while (i < length) {
                                            c = string[i]
                                            if (c.isDigit()) {
                                                i++
                                                exp *= 10
                                                exp += c - '0'
                                            } else {
                                                break
                                            }
                                        }
                                        result.stepResult = createComplete(i)
                                        var data = if (minus) {
                                            -integerPart - fractionPart
                                        } else {
                                            integerPart + fractionPart
                                        }
                                        if (expMinus) {
                                            data /= getPowerOf10(exp)
                                        } else {
                                            data *= getPowerOf10(exp)
                                        }
                                        result.data = data
                                    } else {
                                        result.stepResult = createComplete(i - 2)
                                        result.data = if (minus) {
                                            -integerPart - fractionPart
                                        } else {
                                            integerPart + fractionPart
                                        }
                                    }
                                } else {
                                    result.stepResult = createComplete(i - 2)
                                    result.data = if (minus) {
                                        -integerPart - fractionPart
                                    } else {
                                        integerPart + fractionPart
                                    }
                                }
                                return
                            } else {
                                c = string[i]
                                if (c.isDigit()) {
                                    i++
                                    exp = c - '0'
                                    while (i < length) {
                                        c = string[i]
                                        if (c.isDigit()) {
                                            i++
                                            exp *= 10
                                            exp += c - '0'
                                        } else {
                                            break
                                        }
                                    }
                                    result.stepResult = createComplete(i)
                                    result.data = if (minus) {
                                        -integerPart - fractionPart
                                    } else {
                                        integerPart + fractionPart
                                    } * getPowerOf10(exp)
                                } else {
                                    result.stepResult = createComplete(i - 1)
                                    result.data = if (minus) {
                                        -integerPart - fractionPart
                                    } else {
                                        integerPart + fractionPart
                                    }
                                }
                                return
                            }
                        } else {
                            result.stepResult = createComplete(saveI)
                            result.data = if (minus) {
                                -integerPart - fractionPart
                            } else {
                                integerPart + fractionPart
                            }
                            return
                        }
                    }
                } else {
                    result.stepResult = createComplete(i)
                    result.data = if (minus) {
                        -integerPart - fractionPart
                    } else {
                        integerPart + fractionPart
                    }
                }
            }
            'e', 'E' -> {
                var exp = 0
                i++
                if (i < length) {
                    c = string[i]
                    if (c == '-' || c == '+') {
                        ++i
                        val expMinus = c == '-'
                        if (i < length) {
                            c = string[i]
                            if (c.isDigit()) {
                                i++
                                exp = c - '0'
                                while (i < length) {
                                    c = string[i]
                                    if (c.isDigit()) {
                                        i++
                                        exp *= 10
                                        exp += c - '0'
                                    } else {
                                        break
                                    }
                                }
                                result.stepResult = createComplete(i)
                                var data = if (minus) {
                                    -integerPart - fractionPart
                                } else {
                                    integerPart + fractionPart
                                }
                                if (expMinus) {
                                    data /= getPowerOf10(exp)
                                } else {
                                    data *= getPowerOf10(exp)
                                }
                                result.data = data
                            } else {
                                result.stepResult = createComplete(i - 2)
                                result.data = if (minus) {
                                    -integerPart - fractionPart
                                } else {
                                    integerPart + fractionPart
                                }
                            }
                        } else {
                            result.stepResult = createComplete(i - 2)
                            result.data = if (minus) {
                                -integerPart - fractionPart
                            } else {
                                integerPart + fractionPart
                            }
                        }
                        return
                    } else {
                        c = string[i]
                        if (c.isDigit()) {
                            i++
                            exp = c - '0'
                            while (i < length) {
                                c = string[i]
                                if (c.isDigit()) {
                                    i++
                                    exp *= 10
                                    exp += c - '0'
                                } else {
                                    break
                                }
                            }
                            result.stepResult = createComplete(i)
                            result.data = if (minus) {
                                -integerPart - fractionPart
                            } else {
                                integerPart + fractionPart
                            } * getPowerOf10(exp)
                        } else {
                            result.stepResult = createComplete(i - 1)
                            result.data = if (minus) {
                                -integerPart - fractionPart
                            } else {
                                integerPart + fractionPart
                            }
                        }
                        return
                    }
                } else {
                    result.stepResult = createComplete(saveI)
                    result.data = if (minus) {
                        -integerPart - fractionPart
                    } else {
                        integerPart + fractionPart
                    }
                    return
                }
            }
            else -> {
                result.stepResult = createComplete(i)
                result.data = if (minus) {
                    -integerPart - fractionPart
                } else {
                    integerPart + fractionPart
                }
            }
        }
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        val length = string.length
        if (seek >= length) {
            return -seek
        }

        var i = seek
        do {
            val c = string[i]
            if (c.isDigit()) {
                return if (i == seek) {
                    -seek
                } else {
                    i
                }
            } else if(c == '-' || c == '+') {
                if (i + 1 < length) {
                    if (string[i + 1].isDigit()) {
                        return i
                    } else {
                        i++
                    }
                } else {
                    return i + 1
                }
            } else if (c == '.') {
                if (i + 1 < length) {
                    if (string[i + 1].isDigit()) {
                        return i
                    } else {
                        i++
                    }
                } else {
                    return i + 1
                }
            } else {
                i++
            }
        } while (i < length)

        return i
    }

    override fun resetStep() {
        state = STATE_CHECK_INTEGER_SIGN
        exp = 0
        integer = 0.0
        fraction = 0.0
        sign = 1.0
        expMinus = false
        fractionE = 10.0
    }

    override fun getStepParserResult(string: CharSequence): Double {
        return if (expMinus) {
            sign * (integer + fraction) / getPowerOf10(exp)
        } else {
            sign * (integer + fraction) * getPowerOf10(exp)
        }
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        if (seek >= string.length) {
            return createStepResult(
                seek = seek - seekRollBackMap[state],
                stepCode = if (state == STATE_CHECK_INTEGER_SIGN) {
                    StepCode.EOF
                } else if (!canCompleteStep[state]) {
                    StepCode.INVALID_DOUBLE
                } else {
                    StepCode.COMPLETE
                }
            )
        }

        val c = string[seek]
        when (state) {
            STATE_CHECK_INTEGER_SIGN -> {
                if (c.isDigit()) {
                    state = STATE_INTEGER
                    integer = (c - '0').toDouble()
                    return createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.MAY_COMPLETE
                    )
                } else if (c == '-' || c == '+') {
                    if (c == '-') {
                        sign = -1.0
                    }

                    state = STATE_INTEGER_SIGN_CHECKED
                    return createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.HAS_NEXT
                    )
                } else if(c == '.') {
                    state = STATE_DOT_CHECKED
                    return createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.HAS_NEXT
                    )
                } else {
                    return createStepResult(
                        seek = seek,
                        stepCode = StepCode.INVALID_DOUBLE
                    )
                }
            }
            STATE_INTEGER_SIGN_CHECKED -> {
                if (c.isDigit()) {
                    state = STATE_INTEGER
                    integer = (c - '0').toDouble()
                    return createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.MAY_COMPLETE
                    )
                } else if(c == '.') {
                    state = STATE_DOT_CHECKED_AFTER_SIGN
                    return createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.HAS_NEXT
                    )
                } else {
                    return createStepResult(
                        seek = seek - 1,
                        stepCode = StepCode.INVALID_DOUBLE
                    )
                }
            }
            STATE_DOT_CHECKED_AFTER_SIGN, STATE_DOT_CHECKED, STATE_DOT_CHECKED_AFTER_INTEGER -> {
                return if (c.isDigit()) {
                    state = STATE_FRACTION
                    fraction += (c - '0').toDouble() / fractionE
                    createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.MAY_COMPLETE
                    )
                } else if (c == 'e' || c == 'E') {
                    state = STATE_EXP_CHECK_SIGN_AFTER_DOT
                    createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.HAS_NEXT
                    )
                } else {
                    createStepResult(
                        seek = seek - seekRollBackMap[state],
                        stepCode = if (canCompleteStep[state]) {
                            StepCode.COMPLETE
                        } else {
                            StepCode.INVALID_DOUBLE
                        }
                    )
                }
            }
            STATE_INTEGER -> {
                return if (c.isDigit()) {
                    integer *= 10
                    integer += (c - '0').toDouble()
                    createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.MAY_COMPLETE
                    )
                } else if(c == '.') {
                    state = STATE_DOT_CHECKED_AFTER_INTEGER
                    createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.HAS_NEXT
                    )
                } else if(c == 'e' || c == 'E') {
                    state = STATE_EXP_CHECK_SIGN
                    createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.HAS_NEXT
                    )
                } else {
                    createStepResult(
                        seek = seek,
                        stepCode = StepCode.COMPLETE
                    )
                }
            }
            STATE_FRACTION -> {
                return if (c.isDigit()) {
                    fractionE *= 10
                    fraction += (c - '0').toDouble() / fractionE
                    createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.MAY_COMPLETE
                    )
                } else if(c == 'e' || c == 'E') {
                    state = STATE_EXP_CHECK_SIGN
                    createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.HAS_NEXT
                    )
                } else {
                    createStepResult(
                        seek = seek,
                        stepCode = StepCode.COMPLETE
                    )
                }
            }
            STATE_EXP_CHECK_SIGN, STATE_EXP_CHECK_SIGN_AFTER_DOT -> {
                return if (c.isDigit()) {
                    exp = c - '0'
                    state = STATE_EXP_VALUE
                    createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.MAY_COMPLETE
                    )
                } else if (c == '-') {
                    expMinus = true
                    state = if (state == STATE_EXP_CHECK_SIGN) {
                        STATE_EXP_SIGN_CHECKED
                    } else {
                        STATE_EXP_SIGN_CHECKED_AFTER_DOT
                    }
                    createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.HAS_NEXT
                    )
                } else if (c == '+') {
                    state = if (state == STATE_EXP_CHECK_SIGN) {
                        STATE_EXP_SIGN_CHECKED
                    } else {
                        STATE_EXP_SIGN_CHECKED_AFTER_DOT
                    }
                    createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.HAS_NEXT
                    )
                } else {
                    createStepResult(
                        seek = seek - seekRollBackMap[state],
                        stepCode = StepCode.COMPLETE
                    )
                }
            }
            STATE_EXP_SIGN_CHECKED, STATE_EXP_SIGN_CHECKED_AFTER_DOT -> {
                return if (c.isDigit()) {
                    exp = c - '0'
                    state = STATE_EXP_VALUE
                    createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.MAY_COMPLETE
                    )
                } else {
                    createStepResult(
                        seek = seek - seekRollBackMap[state],
                        stepCode = StepCode.COMPLETE
                    )
                }
            }
            STATE_EXP_VALUE -> {
                return if (c.isDigit()) {
                    exp *= 10
                    exp += c - '0'
                    createStepResult(
                        seek = seek + 1,
                        stepCode = StepCode.MAY_COMPLETE
                    )
                } else {
                    createStepResult(
                        seek = seek,
                        stepCode = StepCode.COMPLETE
                    )
                }
            }
        }

        throw IllegalStateException("Invalid state")
    }

    override fun resetNoStep() {
        noStepSuccessFlag = false
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        val length = string.length
        if (seek >= length) {
            return createStepResult(
                seek = seek,
                stepCode = StepCode.COMPLETE
            )
        }

        val char = string[seek]
        when (state) {
            NO_STEP_CHECK_SIGN_OR_DOT_STATE -> {
                when {
                    char.isDigit() -> {
                        return createStepResult(
                            seek = seek,
                            stepCode = if(noStepSuccessFlag) {
                                StepCode.COMPLETE
                            } else {
                                StepCode.NO_FAILED
                            }
                        )
                    }
                    char == '+' || char == '-' -> {
                        state = NO_STEP_SIGN_CHECKED
                        return createStepResult(
                            seek = seek + 1,
                            stepCode = StepCode.HAS_NEXT
                        )
                    }
                    char == '.' -> {
                        state = NO_STEP_DOT_CHECKED
                        return createStepResult(
                            seek = seek + 1,
                            stepCode = StepCode.HAS_NEXT
                        )
                    }
                    else -> {
                        noStepSuccessFlag = true
                        state = NO_STEP_CHECK_SIGN_OR_DOT_STATE
                        return createStepResult(
                            seek = seek + 1,
                            stepCode = StepCode.MAY_COMPLETE
                        )
                    }
                }
            }
            NO_STEP_SIGN_CHECKED -> {
                when {
                    char.isDigit() -> {
                        return createStepResult(
                            seek = seek,
                            stepCode = if(noStepSuccessFlag) {
                                StepCode.COMPLETE
                            } else {
                                StepCode.NO_FAILED
                            }
                        )
                    }
                    char == '.' -> {
                        state = NO_STEP_DOT_CHECKED_AFTER_SIGN
                        return createStepResult(
                            seek = seek + 1,
                            stepCode = StepCode.HAS_NEXT
                        )
                    }
                    char == '+' || char == '-' -> {
                        noStepSuccessFlag = true
                        state = NO_STEP_CHECK_SIGN_OR_DOT_STATE
                        return createStepResult(
                            seek = seek,
                            stepCode = StepCode.MAY_COMPLETE
                        )
                    }
                    else -> {
                        noStepSuccessFlag = true
                        state = NO_STEP_CHECK_SIGN_OR_DOT_STATE
                        return createStepResult(
                            seek = seek + 1,
                            stepCode = StepCode.MAY_COMPLETE
                        )
                    }
                }
            }
            NO_STEP_DOT_CHECKED, NO_STEP_DOT_CHECKED_AFTER_SIGN -> {
                when {
                    char.isDigit() -> {
                        return createStepResult(
                            seek = if (state == NO_STEP_DOT_CHECKED) {
                                seek -1
                            } else {
                                seek - 2
                            },
                            stepCode = if(noStepSuccessFlag) {
                                StepCode.COMPLETE
                            } else {
                                StepCode.NO_FAILED
                            }
                        )
                    }
                    char == '+' || char == '-' || char == '.' -> {
                        noStepSuccessFlag = true
                        state = NO_STEP_CHECK_SIGN_OR_DOT_STATE
                        return createStepResult(
                            seek = seek,
                            stepCode = StepCode.MAY_COMPLETE
                        )
                    }
                    else -> {
                        noStepSuccessFlag = true
                        state = NO_STEP_CHECK_SIGN_OR_DOT_STATE
                        return createStepResult(
                            seek = seek + 1,
                            stepCode = StepCode.MAY_COMPLETE
                        )
                    }
                }
            }
            else -> throw IllegalStateException("Invalid state")
        }
    }

    override fun clone(): DoubleRule {
        return DoubleRule()
    }
}