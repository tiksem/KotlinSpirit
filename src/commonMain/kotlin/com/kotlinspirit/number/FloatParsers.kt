package com.kotlinspirit.number

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseSeekResult

internal object FloatParsers {
    fun parse(seek: Int, string: CharSequence, invalidFloatErrorCode: Int): ParseSeekResult {
        val length = string.length
        if (seek >= length) {
            return ParseSeekResult(
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
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }

                // Check for infinity
                val after = string[i]
                if (c != '.' && (after == 'i' || after == 'I')) {
                    return if (string.startsWith("nf", i + 1)) {
                        ParseSeekResult(
                            seek = if (string.startsWith("inity", i + 3)) {
                                i + 3 + 5
                            } else {
                                i + 3
                            }
                        )
                    } else {
                        ParseSeekResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        )
                    }
                }

                noMoreDots = c == '.'
                if (!noMoreDots && after == '.') {
                    if (++i >= length) {
                        return ParseSeekResult(
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
                    return ParseSeekResult(
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
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }

                val after = string[i]
                return if (i + 1 < length && after == 'n' && string[i + 1] == 'f') {
                    ParseSeekResult(
                        seek = if (string.startsWith("inity", i + 2)) {
                            i + 2 + 5
                        } else {
                            i + 2
                        }
                    )
                } else {
                    ParseSeekResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }
            }
            'N' -> {
                return if (i + 2 < length && string[++i] == 'a' && string[++i] == 'N') {
                    ParseSeekResult(i + 1)
                } else {
                    ParseSeekResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }
            }
            else -> {
                return ParseSeekResult(
                    seek = seek,
                    parseCode = invalidFloatErrorCode
                )
            }
        }

        if (i >= length) {
            return ParseSeekResult(i)
        }

        when (string[i]) {
            '.' -> {
                if (noMoreDots) {
                    return if (i == seek) {
                        ParseSeekResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        )
                    } else {
                        ParseSeekResult(i)
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
                                    ParseSeekResult(i)
                                } else {
                                    ParseSeekResult(i - 2)
                                }
                            } else {
                                return if (string[i].isDigit()) {
                                    i++
                                    while (i < length && string[i].isDigit()) {
                                        i++
                                    }
                                    ParseSeekResult(i)
                                } else {
                                    ParseSeekResult(i - 1)
                                }
                            }
                        } else {
                            return ParseSeekResult(saveI)
                        }
                    } else {
                        return ParseSeekResult(i - 1)
                    }
                } else {
                    return ParseSeekResult(saveI)
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
                            ParseSeekResult(i)
                        } else {
                            ParseSeekResult(i - 2)
                        }
                    } else {
                        return if (string[i].isDigit()) {
                            i++
                            while (i < length && string[i].isDigit()) {
                                i++
                            }
                            ParseSeekResult(i)
                        } else {
                            ParseSeekResult(i - 1)
                        }
                    }
                } else {
                    return ParseSeekResult(saveI)
                }
            }
            else -> {
                return ParseSeekResult(i)
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

    fun reverseParse(seek: Int, string: CharSequence, invalidFloatErrorCode: Int): ParseSeekResult {
        if (seek < 0 || string.isEmpty()) {
            return ParseSeekResult(
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
                                if (i > 0) {
                                    when (string[--i]) {
                                        in '0'..'9' -> {
                                            --i
                                            eFound = true
                                            continue
                                        }
                                        '.' -> {
                                            if (i > 0) {
                                                when (string[--i]) {
                                                    in '0'..'9' -> {
                                                        --i
                                                        eFound = true
                                                        continue
                                                    }
                                                    else -> {
                                                        return ParseSeekResult(i + 2)
                                                    }
                                                }
                                            } else {
                                                return ParseSeekResult(i + 1)
                                            }
                                        }
                                        else -> return ParseSeekResult(i + 1)
                                    }
                                } else {
                                    return ParseSeekResult(i)
                                }
                            }
                        }
                        '+', '-' -> {
                            if (eFound || dotFound) {
                                return ParseSeekResult(i - 1)
                            }

                            if (i != 0) {
                                when (string[--i]) {
                                    'E', 'e' -> {
                                        if (i > 0) {
                                            when (string[--i]) {
                                                in '0'..'9' -> {
                                                    --i
                                                    eFound = true
                                                    continue
                                                }
                                                '.' -> {
                                                    if (i > 0) {
                                                        when (string[--i]) {
                                                            in '0'..'9' -> {
                                                                --i
                                                                eFound = true
                                                                continue
                                                            }
                                                            else -> {
                                                                return ParseSeekResult(i + 2)
                                                            }
                                                        }
                                                    } else {
                                                        return ParseSeekResult(i + 1)
                                                    }
                                                }
                                                else -> return ParseSeekResult(i + 1)
                                            }
                                        } else {
                                            return ParseSeekResult(i)
                                        }
                                    }
                                    else -> {
                                        return ParseSeekResult(i)
                                    }
                                }
                            } else {
                                return ParseSeekResult(i - 1)
                            }
                        }
                        else -> {
                            break
                        }
                    }
                }

                return ParseSeekResult(i)
            }
            '.' -> {
                var i = seek - 1
                if (i < 0 || string[i--] !in '0'..'9') {
                    return ParseSeekResult(
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

                return ParseSeekResult(i)
            }
            'N' -> {
                if (seek < 2) {
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }

                return if (string[seek - 1] == 'a' && string[seek - 2] == 'N') {
                    ParseSeekResult(seek - 3)
                } else {
                    ParseSeekResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }
            }
            'y' -> {
                if (seek < 7) {
                    return ParseSeekResult(
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
                        ParseSeekResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        )
                    }

                    return ParseSeekResult(i)
                }
            }
            'f' -> {
                if (seek < 2 || string[seek - 1] != 'n') {
                    return ParseSeekResult(
                        seek = seek,
                        parseCode = invalidFloatErrorCode
                    )
                }

                return when (string[seek - 2]) {
                    'i', 'I' -> {
                        ParseSeekResult(
                            seek = if (seek == 2) {
                                -1
                            } else {
                                when (string[seek - 3]) {
                                    '+', '-' -> {
                                        seek - 4
                                    }
                                    else -> seek - 3
                                }
                            }
                        )
                    }
                    else -> {
                        ParseSeekResult(
                            seek = seek,
                            parseCode = invalidFloatErrorCode
                        )
                    }
                }
            }
        }

        return ParseSeekResult(
            seek = seek,
            parseCode = invalidFloatErrorCode
        )
    }
}