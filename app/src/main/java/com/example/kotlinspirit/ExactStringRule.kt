package com.example.kotlinspirit

class ExactStringRule(
    internal val string: String
) : RuleWithDefaultRepeat<CharSequence>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        val str = string.subSequence(seek, string.length)
        return if (str.startsWith(this.string)) {
            createComplete(
                seek = seek + this.string.length
            )
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.STRING_DOES_NOT_MATCH
            )
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<CharSequence>
    ) {
        val str = string.subSequence(seek, string.length)
        if (str.startsWith(this.string)) {
            result.parseResult = createComplete(
                seek = seek + this.string.length
            )
            result.data = string.subSequence(seek, this.string.length + seek)
        } else {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.STRING_DOES_NOT_MATCH
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return string.subSequence(seek, string.length).startsWith(this.string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        val findIndex = string.indexOf(this.string, seek)
        return if (findIndex == seek) {
            -seek
        } else {
            findIndex
        }
    }

    infix fun or(anotherRule: ExactStringRule): OneOfStringRule {
        return OneOfStringRule(listOf(string, anotherRule.string))
    }

    infix fun or(string: String): OneOfStringRule {
        return OneOfStringRule(listOf(this.string, string))
    }
}