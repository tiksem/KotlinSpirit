package com.kotlinspirit.number

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseSeekResult

internal object UIntParsers {
    inline fun parse(
        seek: Int,
        string: CharSequence,
        invalidIntParseCode: Int,
        outOfBoundsParseCode: Int,
        checkOutOfBounds: (ULong, ULong) -> Boolean
    ): ParseSeekResult {
        val length = string.length
        if (seek >= length) {
            return ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        var i = seek
        var result = 0UL
        var successFlag = false
        do {
            val char = string[i++]
            when {
                char in '0'..'9' -> {
                    val resultBefore = result
                    successFlag = true
                    result *= 10u
                    result += (char - '0').toUInt()
                    // check int bounds
                    if (checkOutOfBounds(resultBefore, result)) {
                        return ParseSeekResult(
                            seek = seek,
                            parseCode = outOfBoundsParseCode
                        )
                    }
                }
                successFlag -> {
                    return ParseSeekResult(i - 1)
                }
                else -> {
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = invalidIntParseCode
                    )
                }
            }
        } while (i < length)

        return ParseSeekResult(i)
    }

    inline fun parseWithResult(
        seek: Int,
        string: CharSequence,
        invalidIntParseCode: Int,
        outOfBoundsParseCode: Int,
        checkOutOfBounds: (ULong, ULong) -> Boolean,
        getResult: (ULong?, ParseSeekResult) -> Unit
    ) {
        val length = string.length
        if (seek >= length) {
            getResult(
                null,
                ParseSeekResult(
                    seek = seek,
                    parseCode = ParseCode.EOF
                )
            )
            return
        }

        var i = seek
        var result = 0UL
        var successFlag = false
        do {
            val char = string[i++]
            when {
                char in '0'..'9' -> {
                    val resultBefore = result
                    successFlag = true
                    result *= 10u
                    result += (char - '0').toUInt()
                    // check int bounds
                    if (checkOutOfBounds(resultBefore, result)) {
                        getResult(
                            null,
                            ParseSeekResult(
                                seek = seek,
                                parseCode = outOfBoundsParseCode
                            )
                        )
                        return
                    }
                }
                successFlag -> {
                    getResult(
                        result,
                        ParseSeekResult(i - 1)
                    )
                    return
                }
                else -> {
                    getResult(
                        null,
                        ParseSeekResult(
                            seek = i,
                            parseCode = invalidIntParseCode
                        )
                    )
                    return
                }
            }
        } while (i < length)

        getResult(result, ParseSeekResult(i))
    }

    inline fun reverseParse(
        seek: Int,
        string: CharSequence,
        invalidIntParseCode: Int,
        outOfBoundsParseCode: Int,
        checkOutOfBounds: (ULong, ULong) -> Boolean
    ): ParseSeekResult {
        if (seek < 0) {
            return ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        val ch = string[seek]
        if (ch !in '0'..'9') {
            return ParseSeekResult(
                seek = seek,
                parseCode = invalidIntParseCode
            )
        }

        var i = seek - 1
        var result = (ch - '0').toULong()
        var multiplier = 10UL

        while (i >= 0) {
            val c = string[i]
            when (c) {
                in '0'..'9' -> {
                    val resultBefore = result
                    result += multiplier * (c - '0').toULong()
                    if (checkOutOfBounds(resultBefore, result)) {
                        return ParseSeekResult(
                            seek = seek,
                            parseCode = outOfBoundsParseCode
                        )
                    }
                    multiplier *= 10u
                    --i
                }
                else -> {
                    return ParseSeekResult(i)
                }
            }
        }

        return ParseSeekResult(-1)
    }

    inline fun reverseParseWithResult(
        seek: Int,
        string: CharSequence,
        invalidIntParseCode: Int,
        outOfBoundsParseCode: Int,
        checkOutOfBounds: (ULong, ULong) -> Boolean,
        getResult: (ULong?, ParseSeekResult) -> Unit
    ) {
        if (seek < 0) {
            getResult(
                null,
                ParseSeekResult(
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
                ParseSeekResult(
                    seek = seek,
                    parseCode = invalidIntParseCode
                )
            )
            return
        }

        var i = seek - 1
        var result = (ch - '0').toULong()
        var multiplier = 10UL

        while (i >= 0) {
            val c = string[i]
            when (c) {
                in '0'..'9' -> {
                    val resultBefore = result
                    result += multiplier * (c - '0').toULong()
                    if (checkOutOfBounds(resultBefore, result)) {
                        getResult(
                            null,
                            ParseSeekResult(
                                seek = seek,
                                parseCode = outOfBoundsParseCode
                            )
                        )
                        return
                    }
                    multiplier *= 10u
                    --i
                }
                else -> {
                    break
                }
            }
        }

        getResult(result, ParseSeekResult(i))
    }
}