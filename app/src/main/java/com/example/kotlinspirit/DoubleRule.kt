package com.example.kotlinspirit

class DoubleRule : BaseRule<Double>() {
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
        if (c == '-' || c == '.') {
            i++
            noMoreDots = c == '.'
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

    override fun clone(): DoubleRule {
        return DoubleRule()
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
            } else if(c == '-') {
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

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        TODO("Not yet implemented")
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Double>) {
        super.parseWithResult(seek, string, result)
    }
}