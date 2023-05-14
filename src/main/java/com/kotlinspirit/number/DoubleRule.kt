package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

private inline fun getPowerOf10(exp: Int): Double {
    return when {
        exp > POWERS_OF_10.size || exp < 0 -> Double.POSITIVE_INFINITY
        else -> POWERS_OF_10[exp]
    }
}

class DoubleRule(name: String? = null) : RuleWithDefaultRepeat<Double>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return parseFloatingNumber(
            seek = seek,
            string = string,
            invalidFloatErrorCode = ParseCode.INVALID_DOUBLE
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Double>) {
        val length = string.length
        if (seek >= length) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
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
        when (c) {
            '-', '+', '.' -> {
                i++
                if (i >= length) {
                    result.parseResult = createStepResult(
                        seek = seek,
                        parseCode = ParseCode.INVALID_DOUBLE
                    )
                    return
                }

                // Check for infinity
                val after = string[i]
                if (c != '.' && (after == 'i' || after == 'I')) {
                    i++
                    if (string.startsWith("nf", i)) {
                        result.parseResult = createComplete(
                            seek = if (string.startsWith("inity", i + 2)) {
                                i + 2 + 5
                            } else {
                                i + 2
                            }
                        )
                        result.data = if (minus) {
                            Double.NEGATIVE_INFINITY
                        } else {
                            Double.POSITIVE_INFINITY
                        }
                    } else {
                        result.parseResult = createStepResult(
                            seek = seek,
                            parseCode = ParseCode.INVALID_DOUBLE
                        )
                        result.data = null
                    }
                    return
                }

                noMoreDots = c == '.'
                if (!noMoreDots && after == '.') {
                    if (++i >= length) {
                        result.parseResult = createStepResult(
                            seek = seek,
                            parseCode = ParseCode.INVALID_DOUBLE
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
                        result.parseResult = createStepResult(
                            seek = seek,
                            parseCode = ParseCode.INVALID_DOUBLE
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
                        result.parseResult = createStepResult(
                            seek = seek,
                            parseCode = ParseCode.INVALID_DOUBLE
                        )
                        return
                    }
                }
            }
            in '0'..'9' -> {
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
            }
            'I', 'i' -> {
                // Check for positive infinity
                if (++i >= length) {
                    result.parseResult = createStepResult(
                        seek = seek,
                        parseCode = ParseCode.INVALID_DOUBLE
                    )
                    result.data = null
                    return
                }

                val after = string[i]
                if (after == 'n') {
                    if (++i < length && string[i] == 'f') {
                        i++
                        result.parseResult = createComplete(
                            seek = if (string.startsWith("inity", i)) {
                                i + 5
                            } else {
                                i
                            }
                        )
                        result.data = Double.POSITIVE_INFINITY
                    } else {
                        result.parseResult = createStepResult(
                            seek = seek,
                            parseCode = ParseCode.INVALID_DOUBLE
                        )
                        result.data = null
                    }
                    return
                }
            }
            'N' -> {
                if (i + 2 < length && string[++i] == 'a' && string[++i] == 'N') {
                    result.parseResult = createComplete(seek = i + 1)
                    result.data = Double.NaN
                } else {
                    result.parseResult = createStepResult(
                        seek = seek,
                        parseCode = ParseCode.INVALID_DOUBLE
                    )
                }
                return
            }
            else -> {
                result.parseResult = createStepResult(
                    seek = seek,
                    parseCode = ParseCode.INVALID_DOUBLE
                )
                return
            }
        }

        if (i >= length) {
            result.parseResult = createComplete(i)
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
                    result.parseResult = createComplete(i)
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
                    result.parseResult = createComplete(saveI)
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
                                                if (exp >= 0) {
                                                    exp *= 10
                                                    exp += c - '0'
                                                }
                                            } else {
                                                break
                                            }
                                        }
                                        result.parseResult = createComplete(i)
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
                                        result.parseResult = createComplete(i - 2)
                                        result.data = if (minus) {
                                            -integerPart - fractionPart
                                        } else {
                                            integerPart + fractionPart
                                        }
                                    }
                                } else {
                                    result.parseResult = createComplete(i - 2)
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
                                            if (exp >= 0) {
                                                exp *= 10
                                                exp += c - '0'
                                            }
                                        } else {
                                            break
                                        }
                                    }
                                    result.parseResult = createComplete(i)
                                    result.data = if (minus) {
                                        -integerPart - fractionPart
                                    } else {
                                        integerPart + fractionPart
                                    } * getPowerOf10(exp)
                                } else {
                                    result.parseResult = createComplete(i - 1)
                                    result.data = if (minus) {
                                        -integerPart - fractionPart
                                    } else {
                                        integerPart + fractionPart
                                    }
                                }
                                return
                            }
                        } else {
                            result.parseResult = createComplete(saveI)
                            result.data = if (minus) {
                                -integerPart - fractionPart
                            } else {
                                integerPart + fractionPart
                            }
                            return
                        }
                    } else {
                        result.parseResult = createComplete(i - 1)
                        result.data = if (minus) {
                            -integerPart - fractionPart
                        } else {
                            integerPart + fractionPart
                        }
                    }
                } else {
                    result.parseResult = createComplete(i)
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
                                        if (exp >= 0) {
                                            exp *= 10
                                            exp += c - '0'
                                        }
                                    } else {
                                        break
                                    }
                                }
                                result.parseResult = createComplete(i)
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
                                result.parseResult = createComplete(i - 2)
                                result.data = if (minus) {
                                    -integerPart - fractionPart
                                } else {
                                    integerPart + fractionPart
                                }
                            }
                        } else {
                            result.parseResult = createComplete(i - 2)
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
                                    if (exp >= 0) {
                                        exp *= 10
                                        exp += c - '0'
                                    }
                                } else {
                                    break
                                }
                            }
                            result.parseResult = createComplete(i)
                            result.data = if (minus) {
                                -integerPart - fractionPart
                            } else {
                                integerPart + fractionPart
                            } * getPowerOf10(exp)
                        } else {
                            result.parseResult = createComplete(i - 1)
                            result.data = if (minus) {
                                -integerPart - fractionPart
                            } else {
                                integerPart + fractionPart
                            }
                        }
                        return
                    }
                } else {
                    result.parseResult = createComplete(saveI)
                    result.data = if (minus) {
                        -integerPart - fractionPart
                    } else {
                        integerPart + fractionPart
                    }
                    return
                }
            }
            else -> {
                result.parseResult = createComplete(i)
                result.data = if (minus) {
                    -integerPart - fractionPart
                } else {
                    integerPart + fractionPart
                }
            }
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return floatingNumberHasMatch(seek, string)
    }

    override fun clone(): DoubleRule {
        return this
    }

    override fun ignoreCallbacks(): DoubleRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): DoubleRule {
        return DoubleRule(name)
    }

    override val defaultDebugName: String
        get() = "double"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun isDynamic(): Boolean {
        return false
    }
}