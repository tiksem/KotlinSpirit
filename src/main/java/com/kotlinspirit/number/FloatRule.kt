package com.kotlinspirit.number

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

private inline fun getPowerOf10(exp: Int): Float {
    return when {
        exp > POWERS_OF_10_FLOAT.size || exp < 0 -> Float.POSITIVE_INFINITY
        else -> POWERS_OF_10_FLOAT[exp]
    }
}

internal fun parseFloatingNumber(seek: Int, string: CharSequence, invalidFloatErrorCode: Int): Long {
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

internal fun floatingNumberHasMatch(seek: Int, string: CharSequence): Boolean {
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
                string.startsWith("inf", seek + 1) || string.startsWith("Inf", seek + 1)
            }
        }
        else -> string.startsWith("inf") || string.startsWith("Inf")
    }
}

class FloatRule(name: String? = null) : RuleWithDefaultRepeat<Float>(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return parseFloatingNumber(
            seek = seek,
            string = string,
            invalidFloatErrorCode = ParseCode.INVALID_FLOAT
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Float>) {
        val length = string.length
        if (seek >= length) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            result.data = null
            return
        }

        // Skip integer part
        var i = seek
        var c = string[i]
        var noMoreDots = false
        var integerPart = 0.0f
        var fractionPart = 0.0f
        val minus = c == '-'
        when (c) {
            '-', '+', '.' -> {
                i++
                if (i >= length) {
                    result.parseResult = createStepResult(
                        seek = seek,
                        parseCode = ParseCode.INVALID_FLOAT
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
                            Float.NEGATIVE_INFINITY
                        } else {
                            Float.POSITIVE_INFINITY
                        }
                    } else {
                        result.parseResult = createStepResult(
                            seek = seek,
                            parseCode = ParseCode.INVALID_FLOAT
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
                            parseCode = ParseCode.INVALID_FLOAT
                        )
                        return
                    }
                    noMoreDots = true
                }

                c = string[i]
                if (noMoreDots) {
                    var e = 10.0f
                    if (c.isDigit()) {
                        fractionPart = (c - '0').toFloat() / e
                        i++
                        while (i < length) {
                            c = string[i]
                            if (c.isDigit()) {
                                e *= 10
                                fractionPart += (c - '0').toFloat() / e
                                i++
                            } else {
                                break
                            }
                        }
                    } else {
                        result.parseResult = createStepResult(
                            seek = seek,
                            parseCode = ParseCode.INVALID_FLOAT
                        )
                        return
                    }
                } else {
                    if (c.isDigit()) {
                        integerPart = (c - '0').toFloat()
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
                            parseCode = ParseCode.INVALID_FLOAT
                        )
                        return
                    }
                }
            }
            in '0'..'9' -> {
                integerPart = (c - '0').toFloat()
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
                        parseCode = ParseCode.INVALID_FLOAT
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
                        result.data = Float.POSITIVE_INFINITY
                    } else {
                        result.parseResult = createStepResult(
                            seek = seek,
                            parseCode = ParseCode.INVALID_FLOAT
                        )
                        result.data = null
                    }
                    return
                }
            }
            'N' -> {
                if (i + 2 < length && string[++i] == 'a' && string[++i] == 'N') {
                    result.parseResult = createComplete(seek = i + 1)
                    result.data = Float.NaN
                } else {
                    result.parseResult = createStepResult(
                        seek = seek,
                        parseCode = ParseCode.INVALID_FLOAT
                    )
                }
                return
            }
            else -> {
                result.parseResult = createStepResult(
                    seek = seek,
                    parseCode = ParseCode.INVALID_FLOAT
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
                var e = 10.0f
                if (c.isDigit()) {
                    fractionPart = (c - '0').toFloat() / e
                    i++
                    while (i < length) {
                        c = string[i]
                        if (c.isDigit()) {
                            e *= 10
                            fractionPart += (c - '0').toFloat() / e
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

    override fun clone(): FloatRule {
        return this
    }

    override fun ignoreCallbacks(): FloatRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): FloatRule {
        return FloatRule(name)
    }

    override val defaultDebugName: String
        get() = "float"

    override fun isThreadSafe(): Boolean {
        return true
    }

    override fun isDynamic(): Boolean {
        return false
    }
}