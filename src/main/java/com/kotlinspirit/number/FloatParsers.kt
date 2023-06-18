package com.kotlinspirit.number

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult

private inline fun getPowerOf10(exp: Int): Double {
    return when {
        exp > POWERS_OF_10.size || exp < 0 -> Double.POSITIVE_INFINITY
        else -> POWERS_OF_10[exp]
    }
}

internal object FloatParsers {
    fun parse(seek: Int, string: CharSequence, invalidFloatErrorCode: Int): Long {
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

        when (c) {
            '-', '+', '.' -> {
                i++
                if (i >= length) {
                    return createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }

                // Check for infinity
                val after = string[i]
                if (c != '.' && (after == 'i' || after == 'I')) {
                    return if (string.startsWith("nf", i)) {
                        createComplete(
                            seek = if (string.startsWith("inity", i + 2)) {
                                i + 2 + 5
                            } else {
                                i + 2
                            }
                        )
                    } else {
                        createStepResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        )
                    }
                }

                noMoreDots = c == '.'
                if (!noMoreDots && after == '.') {
                    if (++i >= length) {
                        return createStepResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
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
                        parseCode = invalidFloatErrorCode
                    )
                }
            }
            in '0'..'9' -> {
                i++
                while (i < length && string[i].isDigit()) {
                    i++
                }
            }
            'I', 'i' -> {
                // Check for positive infinity
                if (++i >= length) {
                    return createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }

                val after = string[i]
                if (after == 'i' || after == 'I') {
                    return if (string.startsWith("nf", i)) {
                        createComplete(
                            seek = if (string.startsWith("inity", i + 2)) {
                                i + 2 + 5
                            } else {
                                i + 2
                            }
                        )
                    } else {
                        createStepResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        )
                    }
                }
            }
            'N' -> {
                return if (i + 2 < length && string[++i] == 'a' && string[++i] == 'N') {
                    createComplete(i + 1)
                } else {
                    createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }
            }
            else -> {
                return createStepResult(
                    seek = seek,
                    parseCode = invalidFloatErrorCode
                )
            }
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
                            parseCode = invalidFloatErrorCode
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
    }

    inline fun parseWithResult(
        seek: Int,
        string: CharSequence,
        invalidFloatErrorCode: Int,
        result: (Double?, Long) -> Unit
    ) {
        val length = string.length
        if (seek >= length) {
            result(null, createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            ))
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
                    result(null, createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    ))
                    return
                }

                // Check for infinity
                val after = string[i]
                if (c != '.' && (after == 'i' || after == 'I')) {
                    i++
                    if (string.startsWith("nf", i)) {
                        result(
                            if (minus) {
                                Double.NEGATIVE_INFINITY
                            } else {
                                Double.POSITIVE_INFINITY
                            },
                            createComplete(
                                seek = if (string.startsWith("inity", i + 2)) {
                                    i + 2 + 5
                                } else {
                                    i + 2
                                }
                            )
                        )
                    } else {
                        result(null, createStepResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        ))
                    }
                    return
                }

                noMoreDots = c == '.'
                if (!noMoreDots && after == '.') {
                    if (++i >= length) {
                        result(null, createStepResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        ))
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
                        result(null, createStepResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        ))
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
                        result(null, createStepResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        ))
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
                    result(null, createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    ))
                    return
                }

                val after = string[i]
                if (after == 'n') {
                    if (++i < length && string[i] == 'f') {
                        i++
                        result(Double.POSITIVE_INFINITY, createComplete(
                            seek = if (string.startsWith("inity", i)) {
                                i + 5
                            } else {
                                i
                            }
                        ))
                    } else {
                        result(null, createStepResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        ))
                    }
                    return
                }
            }
            'N' -> {
                if (i + 2 < length && string[++i] == 'a' && string[++i] == 'N') {
                    result(Double.NaN, createComplete(seek = i + 1))
                } else {
                    result(null, createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    ))
                }
                return
            }
            else -> {
                result(null, createStepResult(
                    seek = seek,
                    parseCode = invalidFloatErrorCode
                ))
                return
            }
        }

        if (i >= length) {
            result(if (minus) {
                -integerPart - fractionPart
            } else {
                integerPart + fractionPart
            }, createComplete(i))
            return
        }

        var saveI = i
        when (string[i]) {
            '.' -> {
                if (noMoreDots || ++i >= length) {
                    result(if (minus) {
                        -integerPart - fractionPart
                    } else {
                        integerPart + fractionPart
                    }, createComplete(i))
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
                    result(if (minus) {
                        -integerPart
                    } else {
                        integerPart
                    }, createComplete(saveI))
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
                                        result(data, createComplete(i))
                                    } else {
                                        result(
                                            if (minus) {
                                                -integerPart - fractionPart
                                            } else {
                                                integerPart + fractionPart
                                            },
                                            createComplete(i - 2)
                                        )
                                    }
                                } else {
                                    result(
                                        if (minus) {
                                            -integerPart - fractionPart
                                        } else {
                                            integerPart + fractionPart
                                        },
                                        createComplete(i - 2)
                                    )
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
                                    result(
                                        if (minus) {
                                            -integerPart - fractionPart
                                        } else {
                                            integerPart + fractionPart
                                        } * getPowerOf10(exp),
                                        createComplete(i)
                                    )
                                } else {
                                    result(
                                        if (minus) {
                                            -integerPart - fractionPart
                                        } else {
                                            integerPart + fractionPart
                                        },
                                        createComplete(i - 1)
                                    )
                                }
                                return
                            }
                        } else {
                            result(
                                if (minus) {
                                    -integerPart - fractionPart
                                } else {
                                    integerPart + fractionPart
                                },
                                createComplete(saveI)
                            )
                            return
                        }
                    } else {
                        result(
                            if (minus) {
                                -integerPart - fractionPart
                            } else {
                                integerPart + fractionPart
                            },
                            createComplete(i - 1)
                        )
                    }
                } else {
                    result(
                        if (minus) {
                            -integerPart - fractionPart
                        } else {
                            integerPart + fractionPart
                        },
                        createComplete(i)
                    )
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
                                result(data, createComplete(i))
                            } else {
                                result(
                                    if (minus) {
                                        -integerPart - fractionPart
                                    } else {
                                        integerPart + fractionPart
                                    },
                                    createComplete(i - 2)
                                )
                            }
                        } else {
                            result(
                                if (minus) {
                                    -integerPart - fractionPart
                                } else {
                                    integerPart + fractionPart
                                },
                                createComplete(i - 2)
                            )
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
                            result(
                                if (minus) {
                                    -integerPart - fractionPart
                                } else {
                                    integerPart + fractionPart
                                } * getPowerOf10(exp),
                                createComplete(i)
                            )
                        } else {
                            result(
                                if (minus) {
                                    -integerPart - fractionPart
                                } else {
                                    integerPart + fractionPart
                                },
                                createComplete(i - 1)
                            )
                        }
                        return
                    }
                } else {
                    result(
                        if (minus) {
                            -integerPart - fractionPart
                        } else {
                            integerPart + fractionPart
                        },
                        createComplete(saveI)
                    )
                    return
                }
            }
            else -> {
                result(
                    if (minus) {
                        -integerPart - fractionPart
                    } else {
                        integerPart + fractionPart
                    },
                    createComplete(i)
                )
            }
        }
    }

    fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val length = string.length
        if (seek >= length) {
            return false
        }

        var c = string[seek]
        return when (c) {
            in '0'..'9' -> {
                true
            }
            '.' -> {
                seek + 1 < length && string[seek + 1].isDigit()
            }
            '+', '-' -> {
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
                    string.startsWith("inf", seek + 1) || string.startsWith("Inf", seek + 1)
                }
            }
            else -> string.startsWith("inf") || string.startsWith("Inf") || string.startsWith("NaN")
        }
    }

    fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        if (seek < 0) {
            return false
        }

        val lastChar = string[seek]
        return when (lastChar) {
            in '0'..'9' -> {
                true
            }
            '.' -> {
                seek > 0 && string[seek - 1] in '0'..'9'
            }
            'N' -> {
                seek > 1 && string[seek - 1] == 'a' && string[seek - 2] == 'N'
            }
            'y' -> {
                seek > 6 && string.startsWith("nfinit", startIndex = seek - 6) && string[seek - 7].let {
                    it == 'i' || it == 'I'
                }
            }
            'f' -> {
                seek > 1 && string[seek - 1] == 'n' && string[seek - 2].let {
                    it == 'I' || it == 'i'
                }
            }
            else -> false
        }
    }

    fun reverseParse(seek: Int, string: CharSequence, invalidFloatErrorCode: Int): Long {
        if (seek < 0 || string.isEmpty()) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        val lastChar = string[seek]
        when (lastChar) {
            in '0'..'9' -> {
                var i = seek - 1
                var dotFound = false
                var eFound = false
                while (i >= 0) {
                    when (string[i]) {
                        in '0'..'9' -> {
                            --i
                        }
                        '.' -> {
                            if (dotFound) {
                                break
                            } else {
                                --i
                                dotFound = true
                            }
                        }
                        'e', 'E' -> {
                            if (dotFound || eFound) {
                                break
                            } else {
                                --i
                                eFound = true
                            }
                        }
                        '+', '-' -> {
                            if (eFound || dotFound) {
                                return createComplete(i - 1)
                            }

                            return if (i != 0) {
                                when (string[--i]) {
                                    'E', 'e' -> {
                                        --i
                                        continue
                                    }
                                    else -> {
                                        createComplete(i)
                                    }
                                }
                            } else {
                                createComplete(i - 1)
                            }
                        }
                    }
                }

                return createComplete(i)
            }
            '.' -> {
                var i = seek - 1
                if (i < 0 || string[i--] !in '0'..'9') {
                    return createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }

                while (i >= 0) {
                    when (string[i]) {
                        in '0'..'9' -> {
                            --i
                        }
                        '+', '-' -> {
                            --i
                            break
                        }
                    }
                }

                return createComplete(i)
            }
            'N' -> {
                if (seek < 2) {
                    return createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }

                return if (string[seek - 1] == 'a' && string[seek - 2] == 'N') {
                    createComplete(seek - 3)
                } else {
                    createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }
            }
            'y' -> {
                if (seek < 7) {
                    return createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }

                var i = seek - 6
                if (string.startsWith("nfinit", startIndex = i)) {
                    i--
                    val firstChar = string[i--]
                    if (firstChar == 'i' || firstChar == 'I') {
                        if (i >= 0) {
                            val sign = string[i]
                            if (sign == '-' || sign == '+') {
                                i--
                            }
                        }
                    } else {
                        createStepResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        )
                    }

                    return createComplete(i)
                }
            }
            'f' -> {
                if (seek < 2 || string[seek - 1] != 'n') {
                    return createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }

                return when (string[seek - 2]) {
                    'i', 'I' -> {
                        createComplete(seek = if (seek == 2) {
                            -1
                        } else {
                            when (string[seek - 3]) {
                                '+', '-' -> {
                                    seek - 4
                                }
                                else -> seek - 3
                            }
                        })
                    }
                    else -> {
                        createStepResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        )
                    }
                }
            }
        }

        return createStepResult(
            seek = seek,
            parseCode = invalidFloatErrorCode
        )
    }

    private fun getFractionPart(string: CharSequence, startSeek: Int, endSeek: Int): Double {
        var i = startSeek
        var result = 0.0
        var multiplier = 0.1
        while (i < endSeek) {
            result += multiplier * (string[i] - '0')
            multiplier *= 0.1
            i++
        }

        return result
    }

    private fun getExpPart(string: CharSequence, startSeek: Int, endSeek: Int): Int {
        var i = startSeek
        var result = 0
        var multiplier = 1
        while (i < endSeek) {
            result += multiplier * (string[i] - '0')
            if (result > MAX_DOUBLE_EXP) {
                return MAX_DOUBLE_EXP
            }

            multiplier *= 10
            i++
        }

        return result
    }

    private fun getIntegerPart(string: CharSequence, startSeek: Int, endSeek: Int): Double {
        var i = startSeek
        var result = 0.0
        var multiplier = 1.0
        while (i < endSeek) {
            result += multiplier * (string[i] - '0')
            multiplier *= 10
            i++
        }

        return result
    }

    inline fun reverseParseWithResult(
        seek: Int,
        string: CharSequence,
        invalidFloatErrorCode: Int,
        result: (Double?, Long) -> Unit
    ) {
        if (seek < 0 || string.isEmpty()) {
            result(null, createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            ))
            return
        }

        val lastChar = string[seek]
        var fractionPart = 0.0
        var expPart = 0
        when (lastChar) {
            in '0'..'9' -> {
                var dotFound = false
                var i = seek
                while (i >= 0) {
                    val c = string[i]
                    when (c) {
                        in '0'..'9' -> {
                            --i
                        }
                        '.' -> {
                            fractionPart = getFractionPart(
                                string = string,
                                startSeek = i + 1,
                                endSeek = seek + 1
                            )
                            dotFound = true
                            --i
                            break
                        }
                        'e', 'E' -> {
                            if (i != 0 && string[i - 1].let { it in '0'..'9' || it == '.' }) {
                                expPart = getExpPart(
                                    string = string,
                                    startSeek = i + 1,
                                    endSeek = seek + 1
                                )
                                --i
                            } else {
                                result(
                                    getIntegerPart(
                                        string = string,
                                        startSeek = i + 1,
                                        endSeek = seek + 1
                                    ),
                                    createComplete(i)
                                )
                                return
                            }
                            break
                        }
                        '+', '-' -> {
                            if (i == 0) {
                                result(
                                    getIntegerPart(
                                        string = string,
                                        startSeek = i + 1,
                                        endSeek = seek + 1
                                    ),
                                    createComplete(i - 1)
                                )
                                return
                            } else {
                                --i
                                when (string[i]) {
                                    'e', 'E' -> {
                                        if (i != 0 && string[i - 1].let { it in '0'..'9' || it == '.' }) {
                                            expPart = getExpPart(
                                                string = string,
                                                startSeek = i + 1,
                                                endSeek = seek + 1
                                            )
                                            if (c == '-') {
                                                expPart = -expPart
                                            }
                                            --i
                                        } else {
                                            result(
                                                getIntegerPart(
                                                    string = string,
                                                    startSeek = i + 1,
                                                    endSeek = seek + 1
                                                ).let {
                                                    if (c == '-') {
                                                        -it
                                                    } else {
                                                        it
                                                    }
                                                },
                                                createComplete(i)
                                            )
                                            return
                                        }
                                        break
                                    }
                                    else -> {
                                        result(
                                            getIntegerPart(
                                                string = string,
                                                startSeek = i + 1,
                                                endSeek = seek + 1
                                            ).let {
                                                if (c == '-') {
                                                    -it
                                                } else {
                                                    it
                                                }
                                            },
                                            createComplete(i)
                                        )
                                        return
                                    }
                                }
                            }
                        }
                        else -> {
                            result(
                                getIntegerPart(
                                    string = string,
                                    startSeek = i + 1,
                                    endSeek = seek + 1
                                ),
                                createComplete(i)
                            )
                            return
                        }
                    }
                }

                var integerPart = 0.0
                var multiplier = 1.0
                if (dotFound) {
                    while (i >= 0) {
                        val c = string[i]
                        when (c) {
                            in '0'..'9' -> {
                                integerPart += multiplier * (c - '0')
                                --i
                            }
                            '+', '-' -> {
                                --i
                                integerPart = -integerPart
                                fractionPart = -fractionPart
                                break
                            }
                            else -> {
                                break
                            }
                        }
                    }
                } else {
                    outer@ while (i >= 0) {
                        val c = string[i]
                        when (c) {
                            in '0'..'9' -> {
                                --i
                            }
                            '.' -> {
                                fractionPart = getFractionPart(
                                    string = string,
                                    startSeek = i + 1,
                                    endSeek = seek + 1
                                )
                                --i
                                while (i >= 0) {
                                    val cc = string[i]
                                    when (cc) {
                                        in '0'..'9' -> {
                                            integerPart += multiplier * (cc - '0')
                                            multiplier *= 10
                                            --i
                                        }
                                        '+', '-' -> {
                                            fractionPart = -fractionPart
                                            integerPart = -integerPart
                                            --i
                                            break@outer
                                        }
                                        else -> break@outer
                                    }
                                }
                            }
                            '+', '-' -> {
                                if (i == 0) {
                                    result(
                                        getIntegerPart(
                                            string = string,
                                            startSeek = i + 1,
                                            endSeek = seek + 1
                                        ),
                                        createComplete(i - 1)
                                    )
                                    return
                                } else {
                                    --i
                                    when (string[i]) {
                                        'e', 'E' -> {
                                            expPart = getExpPart(
                                                string = string,
                                                startSeek = i + 1,
                                                endSeek = seek + 1
                                            )
                                            if (c == '-') {
                                                expPart = -expPart
                                            }
                                        }
                                        else -> {
                                            val integerPart = getIntegerPart(
                                                string = string,
                                                startSeek = i + 1,
                                                endSeek = seek + 1
                                            )
                                            result(
                                                if (c == '-') {
                                                    -integerPart
                                                } else {
                                                    integerPart
                                                },
                                                createComplete(i)
                                            )
                                            return
                                        }
                                    }
                                    --i
                                }
                            }
                            else -> {
                                result(
                                    getIntegerPart(
                                        string = string,
                                        startSeek = i + 1,
                                        endSeek = seek + 1
                                    ),
                                    createComplete(i)
                                )
                                return
                            }
                        }
                    }

                    result(
                        (integerPart + fractionPart) * POWERS_OF_10[expPart],
                        createComplete(i)
                    )
                    return
                }
            }
            '.' -> {
                var i = seek - 1
                if (i < 0) {
                    result(null, createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    ))
                    return
                }

                val lastDigit = string[i--]
                if (!lastDigit.isDigit()) {
                    result(null, createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    ))
                    return
                }

                var value = (lastDigit - '0').toDouble()
                var multiplier = 10.0
                while (i >= 0) {
                    val digit = string[i]
                    when (digit) {
                        in '0'..'9' -> {
                            value += multiplier * (digit - '0')
                            multiplier *= 10.0
                        }
                        '+' -> {
                            --i
                           break
                        }
                        '-' -> {
                            --i
                            value = -value
                            break
                        }
                        else -> {
                            break
                        }
                    }
                    --i
                }

                result(value, createComplete(i))
                return
            }
            'N' -> {
                if (seek < 2) {
                    result(null, createStepResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    ))
                    return
                }

                if (string[seek - 1] == 'a' && string[seek - 2] == 'N') {
                    result(Double.NaN, createComplete(seek - 3))
                } else {
                    result(
                        null,
                        createStepResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        )
                    )
                }

                return
            }
            'y' -> {
                var i = seek - 6
                if (i >= 1 && string.startsWith("nfinit", startIndex = i)) {
                    i--
                    val firstChar = string[i--]
                    if (firstChar == 'i' || firstChar == 'I') {
                        var v = Double.POSITIVE_INFINITY
                        if (i >= 0) {
                            val sign = string[i]
                            when (sign) {
                                '+' -> {
                                    i--
                                }
                                '-' -> {
                                    i--
                                    v = -v
                                }
                            }
                        }
                        result(v, createComplete(i))
                        return
                    } else {
                        result(
                            null,
                            createStepResult(
                                seek = seek,
                                parseCode = invalidFloatErrorCode
                            )
                        )
                        return
                    }
                }
            }
            'f' -> {
                if (seek < 2 || string[seek - 1] != 'n') {
                    result(
                        null,
                        createStepResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        )
                    )
                    return
                }

                when (string[seek - 2]) {
                    'i', 'I' -> {
                        var value = Double.POSITIVE_INFINITY
                        val resultSeek = if (seek == 2) {
                            -1
                        } else {
                            when (string[seek - 3]) {
                                '+' -> {
                                    seek - 4
                                }
                                '-' -> {
                                    value = -value
                                    seek - 4
                                }
                                else -> seek - 3
                            }
                        }
                        result(value, createComplete(seek = resultSeek))
                        return
                    }
                    else -> {
                        result(
                            null,
                            createStepResult(
                                seek = seek,
                                parseCode = invalidFloatErrorCode
                            )
                        )
                        return
                    }
                }
            }
        }
        result(
            null,
            createStepResult(
                seek = seek,
                parseCode = invalidFloatErrorCode
            )
        )
    }
}