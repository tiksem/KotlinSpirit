package com.example.kotlinspirit

class ExactCharRule(
    private val char: Char
) : CharRule() {
    override fun parse(seek: Int, string: CharSequence): Long {
        if (seek >= string.length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        return if (string[seek] == char) {
            createStepResult(
                seek = seek + 1,
                parseCode = ParseCode.COMPLETE
            )
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        if (seek >= string.length) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            return
        }

        if (string[seek] == char) {
            result.parseResult = createStepResult(
                seek = seek + 1,
                parseCode = ParseCode.COMPLETE
            )
            result.data = char
        } else {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return string.length < seek && string[seek] == char
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        if (string[seek] != char) {
            return seek + 1
        } else {
            return -seek - 1
        }
    }

    override fun clone(): ExactCharRule {
        return this
    }
}