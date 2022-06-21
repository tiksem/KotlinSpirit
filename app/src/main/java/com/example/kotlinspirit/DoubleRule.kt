package com.example.kotlinspirit

private inline fun getPowerOf10(exp: Int): Double {
    return if (exp > POWERS_OF_10.size) {
        Double.POSITIVE_INFINITY
    } else {
        POWERS_OF_10[exp]
    }
}

class DoubleRule : RuleWithDefaultRepeat<Double>() {
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
                    result.stepResult = createComplete(saveI)
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
        TODO("Not yet implemented")
    }

    override fun getStepParserResult(string: CharSequence): Double {
        TODO("Not yet implemented")
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        TODO("Not yet implemented")
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        TODO("Not yet implemented")
    }

    override fun clone(): DoubleRule {
        return DoubleRule()
    }
}