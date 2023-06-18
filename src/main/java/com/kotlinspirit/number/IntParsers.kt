package com.kotlinspirit.number

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult

internal object IntParsers {
    inline fun parse(
        seek: Int,
        string: CharSequence,
        invalidIntParseCode: Int,
        outOfBoundsParseCode: Int,
        checkOutOfBounds: (Long) -> Boolean
    ): Long {
        val length = string.length
        if (seek >= length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        var i = seek
        var result = 0L
        var sign = 1
        var successFlag = false
        do {
            val char = string[i++]
            when {
                char == '-' -> {
                    if (successFlag) {
                        return createComplete(i)
                    } else if (sign == 1) {
                        sign = -1
                    }
                }
                char == '+' -> {
                    if (successFlag) {
                        return createComplete(i)
                    }
                }
                char in '0'..'9' -> {
                    successFlag = true
                    result *= 10
                    result += char - '0'
                    // check int bounds
                    if (checkOutOfBounds(result)) {
                        return createStepResult(
                            seek = seek,
                            parseCode = outOfBoundsParseCode
                        )
                    }
                }
                successFlag -> {
                    return createComplete(i - 1)
                }
                else -> {
                    return createStepResult(
                        seek = seek,
                        parseCode = invalidIntParseCode
                    )
                }
            }
        } while (i < length)

        return if (successFlag) {
            createComplete(i)
        } else {
            createStepResult(
                seek = seek,
                parseCode = invalidIntParseCode
            )
        }
    }

    inline fun parseWithResult(
        seek: Int,
        string: CharSequence,
        invalidIntParseCode: Int,
        outOfBoundsParseCode: Int,
        checkOutOfBounds: (Long) -> Boolean,
        getResult: (Long?, Long) -> Unit
    ) {
        val length = string.length
        if (seek >= length) {
            getResult(
                null,
                createStepResult(
                    seek = seek,
                    parseCode = ParseCode.EOF
                )
            )

            return
        }

        var i = seek
        var sign = 1
        var result = 0L
        var successFlag = false
        do {
            val char = string[i++]
            when {
                (char == '-' || char == '+') && !successFlag -> {
                    when {
                        i == seek + 1 -> {
                            if (char == '-') {
                                sign = -1
                            }
                        }
                        else -> {
                            getResult(
                                null,
                                createStepResult(
                                    seek = seek,
                                    parseCode = invalidIntParseCode
                                )
                            )
                            return
                        }
                    }
                }
                char in '0'..'9' -> {
                    successFlag = true
                    result *= 10
                    result += char - '0'
                    // check int bounds
                    if (checkOutOfBounds(result)) {
                        getResult(
                            null,
                            createStepResult(
                                seek = seek,
                                parseCode = outOfBoundsParseCode
                            )
                        )
                        return
                    }
                }
                successFlag -> {
                    getResult(
                        result * sign,
                        createComplete(i - 1)
                    )
                    return
                }
                else -> {
                    getResult(
                        null,
                        createStepResult(
                            seek = seek,
                            parseCode = invalidIntParseCode
                        )
                    )

                    return
                }
            }
        } while (i < length)

        if (successFlag) {
            getResult(
                result * sign,
                createComplete(i)
            )
        } else {
            getResult(
                null,
                createStepResult(
                    seek = seek,
                    parseCode = invalidIntParseCode
                )
            )
        }
    }

    inline fun reverseParse(
        seek: Int,
        string: CharSequence,
        invalidIntParseCode: Int,
        outOfBoundsParseCode: Int,
        checkOutOfBounds: (Long) -> Boolean
    ): Long {
        if (seek < 0) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        val ch = string[seek]
        if (ch !in '0'..'9') {
            return createStepResult(
                seek = seek,
                parseCode = invalidIntParseCode
            )
        }

        var i = seek - 1
        var result = (ch - '0').toLong()
        var multiplier = 10L

        while (i >= 0) {
            val c = string[i]
            when (c) {
                in '0'..'9' -> {
                    result += multiplier * (c - '0')
                    if (checkOutOfBounds(result)) {
                        return createStepResult(
                            seek = seek,
                            parseCode = outOfBoundsParseCode
                        )
                    }
                    multiplier *= 10
                    --i
                }
                '+', '-' -> {
                    return createComplete(i - 1)
                }
                else -> {
                    return createComplete(i)
                }
            }
        }

        return createComplete(-1)
    }

    inline fun reverseParseWithResult(
        seek: Int,
        string: CharSequence,
        invalidIntParseCode: Int,
        outOfBoundsParseCode: Int,
        checkOutOfBounds: (Long) -> Boolean,
        getResult: (Long?, Long) -> Unit
    ) {
        if (seek < 0) {
            getResult(
                null,
                createStepResult(
                    seek = seek,
                    parseCode = ParseCode.EOF
                )
            )
            return
        }

        val ch = string[seek]
        if (ch !in '0'..'9') {
            getResult(
                null,
                createStepResult(
                    seek = seek,
                    parseCode = invalidIntParseCode
                )
            )
            return
        }

        var i = seek - 1
        var result = (ch - '0').toLong()
        var multiplier = 10L

        while (i >= 0) {
            val c = string[i]
            when (c) {
                in '0'..'9' -> {
                    result += multiplier * (c - '0')
                    if (checkOutOfBounds(result)) {
                        getResult(
                            null,
                            createStepResult(
                                seek = seek,
                                parseCode = outOfBoundsParseCode
                            )
                        )
                        return
                    }
                    multiplier *= 10
                    --i
                }
                '+' -> {
                    getResult(result, createComplete(i - 1))
                    return
                }
                '-' -> {
                    getResult(-result, createComplete(i - 1))
                    return
                }
                else -> {
                    break
                }
            }
        }

        getResult(result, createComplete(i))
    }
}