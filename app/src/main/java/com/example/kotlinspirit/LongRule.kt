package com.example.kotlinspirit

open class LongRule : RuleWithDefaultRepeat<Long>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        val length = string.length
        if (seek >= length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        var i = seek
        var result = 0
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
                !successFlag && char == '0' -> {
                    return if (i >= length || string[i] !in '0'..'9') {
                        createComplete(i)
                    } else {
                        createStepResult(
                            seek = i,
                            parseCode = ParseCode.INT_STARTED_FROM_ZERO
                        )
                    }
                }
                char in '0'..'9' -> {
                    successFlag = true
                    result *= 10
                    result += char - '0'
                    // check int bounds
                    if (result < 0) {
                        return createStepResult(
                            seek = i,
                            parseCode = ParseCode.INT_OUT_OF_BOUNDS
                        )
                    }
                }
                successFlag -> {
                    return createComplete(i - 1)
                }
                else -> {
                    return createStepResult(
                        seek = i,
                        parseCode = ParseCode.INVALID_INT
                    )
                }
            }
        } while (i < length)

        return createComplete(i)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<Long>) {
        val length = string.length
        if (seek >= length) {
            r.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
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
                char == '-' && !successFlag -> {
                    when {
                        successFlag -> {
                            r.data = result * sign
                            r.parseResult = createComplete(i)
                            return
                        }
                        sign == 1 -> {
                            sign = -1
                        }
                        else -> {
                            r.parseResult = createStepResult(
                                seek = i,
                                parseCode = ParseCode.INVALID_INT
                            )
                        }
                    }
                }
                !successFlag && char == '0' -> {
                    if (i >= length || string[i] !in '0'..'9') {
                        r.data = result * sign
                        r.parseResult = createComplete(i)
                    } else {
                        r.parseResult = createStepResult(
                            seek = i,
                            parseCode = ParseCode.INT_STARTED_FROM_ZERO
                        )
                    }
                    return
                }
                char in '0'..'9' -> {
                    successFlag = true
                    result *= 10
                    result += char - '0'
                    // check int bounds
                    if (result < 0) {
                        r.parseResult = createStepResult(
                            seek = i,
                            parseCode = ParseCode.INT_OUT_OF_BOUNDS
                        )
                        return
                    }
                }
                successFlag -> {
                    r.data = result * sign
                    r.parseResult = createComplete(i - 1)
                    return
                }
                else -> {
                    r.parseResult = createStepResult(
                        seek = i,
                        parseCode = ParseCode.INVALID_INT
                    )
                    return
                }
            }
        } while (i < length)

        r.data = result * sign
        r.parseResult = createComplete(i)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val length = string.length
        if (seek >= length) {
            return false
        }

        val char = string[seek]
        return if (char == '-') {
            seek < length - 1 && string[seek + 1] in '0'..'9'
        } else {
            char in '0'..'9'
        }
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        val length = string.length
        if (seek >= length) {
            return seek
        }

        var i = seek
        var noSuccess = false
        do {
            val char = string[i]
            when {
                char == '-' && !noSuccess -> {
                    if (i < length - 1) {
                        if (string[i + 1] in '0'..'9') {
                            return -seek - 1
                        } else {
                            noSuccess = true
                            i+=2
                        }
                    } else {
                        return i + 1
                    }
                }
                char in '0'..'9' -> {
                    return if (noSuccess) {
                        i
                    } else {
                        -i - 1
                    }
                }
                else -> {
                    i++
                }
            }
        } while (i < length)

        return i
    }

    override fun clone(): LongRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): LongRule {
        return DebugLongRule(name ?: "long")
    }
}

private class DebugLongRule(override val name: String): LongRule(), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<Long>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, r)
        DebugEngine.ruleParseEnded(this, r.parseResult)
    }
}