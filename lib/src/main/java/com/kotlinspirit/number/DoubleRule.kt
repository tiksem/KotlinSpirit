package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

private inline fun getPowerOf10(exp: Int): Double {
    return if (exp > POWERS_OF_10.size) {
        Double.POSITIVE_INFINITY
    } else {
        POWERS_OF_10[exp]
    }
}

open class DoubleRule : RuleWithDefaultRepeat<Double>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        val length = string.length
        if (seek >= length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
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
                    parseCode = ParseCode.INVALID_DOUBLE
                )
            }

            noMoreDots = c == '.'
            if (!noMoreDots && string[i] == '.') {
                if (++i >= length) {
                    return createStepResult(
                        seek = seek,
                        parseCode = ParseCode.INVALID_DOUBLE
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
                    parseCode = ParseCode.INVALID_DOUBLE
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
                parseCode = ParseCode.INVALID_DOUBLE
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
                            parseCode = ParseCode.INVALID_DOUBLE
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
                    } else {
                        return createComplete(i - 1)
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
        if (minus || c == '+' || c == '.') {
            i++
            if (i >= length) {
                result.parseResult = createStepResult(
                    seek = seek,
                    parseCode = ParseCode.INVALID_DOUBLE
                )
                return
            }

            noMoreDots = c == '.'
            if (!noMoreDots && string[i] == '.') {
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
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.INVALID_DOUBLE
            )
            return
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
                                                exp *= 10
                                                exp += c - '0'
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
                                            exp *= 10
                                            exp += c - '0'
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
                                        exp *= 10
                                        exp += c - '0'
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
                                    exp *= 10
                                    exp += c - '0'
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
        val length = string.length
        if (seek >= length) {
            return false
        }

        var c = string[seek]
        return when {
            c.isDigit() -> {
                true
            }
            c == '.' -> {
                seek + 1 < length && string[seek + 1].isDigit()
            }
            c == '+' || c == '-' -> {
                if (seek + 1 < length) {
                    c = string[seek + 1]
                    if (c.isDigit()) {
                        true
                    } else if (c == '.') {
                        seek + 2 < length && string[seek + 2].isDigit()
                    } else {
                        false
                    }
                } else {
                    false
                }
            }
            else -> false
        }
    }

    override fun clone(): DoubleRule {
        return this
    }

    override fun ignoreCallbacks(): DoubleRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): DoubleRule {
        return DebugDoubleRule(name ?: "double")
    }

    override fun isThreadSafe(): Boolean {
        return true
    }
}

private class DebugDoubleRule(override val name: String) : DoubleRule(), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Double>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}