package com.kotlinspirit.number

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseSeekResult

internal object IntParsers {
    inline fun parseInt(
        seek: Int,
        radix: Int,
        string: CharSequence,
        invalidIntParseCode: Int,
        outOfBoundsParseCode: Int,
        onResult: (Int) -> Unit
    ): ParseSeekResult {
        var negative = false
        var i = seek
        val len = string.length
        var limit = -Int.MAX_VALUE
        var hasSign = false

        if (i < len) {
            val firstChar: Char = string[i]
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true
                    limit = Int.MIN_VALUE
                } else if (firstChar != '+') {
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = invalidIntParseCode
                    )
                }
                if (i > len - 2) { // Cannot have lone "+" or "-"
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = invalidIntParseCode
                    )
                }
                i++
                hasSign = true
            }
            val multmin: Int = limit / radix
            var result = 0
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                val digit: Int = Character.digit(string[i++], radix)
                if (digit < 0) {
                    if (i > seek + 1) {
                        i--
                        break
                    } else {
                        return ParseSeekResult(
                            seek = seek,
                            parseCode = invalidIntParseCode
                        )
                    }
                } else if (result < multmin) {
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = outOfBoundsParseCode
                    )
                }

                result *= radix
                if (result < limit + digit) {
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = outOfBoundsParseCode
                    )
                }
                result -= digit
            }
            onResult(if (negative) result else -result)

            return if (hasSign && i < seek + 2) {
                ParseSeekResult(
                    seek = seek,
                    parseCode = invalidIntParseCode
                )
            } else {
                ParseSeekResult(i)
            }
        } else {
            return ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }
    }

    inline fun parseLong(
        seek: Int,
        radix: Int,
        string: CharSequence,
        onResult: (Long) -> Unit
    ): ParseSeekResult {
        var negative = false
        var i = seek
        val len = string.length
        var limit = -Long.MAX_VALUE

        if (i < len) {
            val firstChar: Char = string[i]
            var hasSign = false
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true
                    limit = Long.MIN_VALUE
                } else if (firstChar != '+') {
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = ParseCode.INVALID_LONG
                    )
                }
                if (i > len - 2) { // Cannot have lone "+" or "-"
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = ParseCode.INVALID_LONG
                    )
                }
                i++
                hasSign = true
            }
            val multmin = limit / radix
            var result = 0L
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                val digit: Int = Character.digit(string[i++], radix)
                if (digit < 0) {
                    if (i > seek + 1) {
                        i--
                        break
                    } else {
                        return ParseSeekResult(
                            seek = seek,
                            parseCode = ParseCode.INVALID_LONG
                        )
                    }
                } else if (result < multmin) {
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = ParseCode.LONG_OUT_OF_BOUNDS
                    )
                }
                result *= radix
                if (result < limit + digit) {
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = ParseCode.LONG_OUT_OF_BOUNDS
                    )
                }
                result -= digit
            }
            onResult(if (negative) result else -result)

            return if (hasSign && i < seek + 2) {
                ParseSeekResult(
                    seek = seek,
                    parseCode = ParseCode.INVALID_LONG
                )
            } else {
                ParseSeekResult(i)
            }
        } else {
            return ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }
    }

    inline fun reverseParse(
        seek: Int,
        string: CharSequence,
        invalidIntParseCode: Int,
        outOfBoundsParseCode: Int,
        checkOutOfBounds: (Long) -> Boolean
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
        var result = (ch - '0').toLong()
        var multiplier = 10L

        while (i >= 0) {
            val c = string[i]
            when (c) {
                in '0'..'9' -> {
                    result += multiplier * (c - '0')
                    if (checkOutOfBounds(result)) {
                        return ParseSeekResult(
                            seek = seek,
                            parseCode = outOfBoundsParseCode
                        )
                    }
                    multiplier *= 10
                    --i
                }
                '+', '-' -> {
                    return ParseSeekResult(i - 1)
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
        checkOutOfBounds: (Long) -> Boolean,
        getResult: (Long?, ParseSeekResult) -> Unit
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
                            ParseSeekResult(
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
                    getResult(result, ParseSeekResult(i - 1))
                    return
                }
                '-' -> {
                    getResult(-result, ParseSeekResult(i - 1))
                    return
                }
                else -> {
                    break
                }
            }
        }

        getResult(result, ParseSeekResult(i))
    }
}