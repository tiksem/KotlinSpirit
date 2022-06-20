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
        if (c == '-') {
            i++
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
        TODO("Not yet implemented")
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        TODO("Not yet implemented")
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Double>) {
        super.parseWithResult(seek, string, result)
    }
}