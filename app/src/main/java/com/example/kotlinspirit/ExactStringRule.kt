package com.example.kotlinspirit

class ExactStringRule(
    private val string: String
) : RuleWithDefaultRepeat<CharSequence>() {
    private var beginSeek = -1

    override fun parse(seek: Int, string: CharSequence): Long {
        val str = string.subSequence(seek, string.length)
        return if (str.startsWith(this.string)) {
            createComplete(
                seek = seek + this.string.length
            )
        } else {
            createStepResult(
                seek = seek,
                stepCode = StepCode.STRING_DOES_NOT_MATCH
            )
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<CharSequence>
    ) {
        val str = string.subSequence(seek, string.length)
        if (str.startsWith(this.string)) {
            result.stepResult = createComplete(
                seek = seek + this.string.length
            )
            result.data = string.subSequence(seek, this.string.length + seek)
        } else {
            result.stepResult = createStepResult(
                seek = seek,
                stepCode = StepCode.STRING_DOES_NOT_MATCH
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return string.subSequence(seek, string.length).startsWith(this.string)
    }

    override fun resetStep() {
        beginSeek = -1
    }

    override fun getStepParserResult(string: CharSequence): CharSequence {
        return string
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        if (beginSeek < 0) {
            beginSeek = seek
        }

        val i = seek - beginSeek
        if (i >= this.string.length) {
            return createStepResult(
                seek = seek,
                stepCode = StepCode.COMPLETE
            )
        }

        if (seek >= string.length) {
            return createStepResult(
                seek = seek,
                stepCode = StepCode.EOF
            )
        }

        return if (string[seek] == this.string[seek - beginSeek]) {
            createStepResult(seek + 1, StepCode.HAS_NEXT)
        } else {
            createStepResult(seek, StepCode.STRING_DOES_NOT_MATCH)
        }
    }

    override fun resetNoStep() {
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        val findIndex = string.indexOf(this.string, seek)
        return if (findIndex == seek) {
            -seek
        } else {
            findIndex
        }
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        throw UnsupportedOperationException()
    }

    override fun clone(): ExactStringRule {
        return ExactStringRule(string)
    }
}