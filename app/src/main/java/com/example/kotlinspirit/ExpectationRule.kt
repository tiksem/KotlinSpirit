package com.example.kotlinspirit

class ExpectationRule(
    private val a: Rule<*>,
    private val b: Rule<*>
) : RuleWithDefaultRepeat<CharSequence>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        val aResult = a.parse(seek, string)
        if (aResult.getParseCode().isError()) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.COMPLETE
            )
        }

        val bResult = b.parse(aResult.getSeek(), string)
        val parseCode = bResult.getParseCode()
        return if (parseCode.isError()) {
            createStepResult(
                seek = seek,
                parseCode = parseCode
            )
        } else {
            bResult
        }
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<CharSequence>
    ) {
        val parseResult = parse(seek, string)
        result.parseResult = parseResult
        if (parseResult.getParseCode().isNotError()) {
            result.data = string.subSequence(seek, parseResult.getSeek())
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val aResult = a.parse(seek, string)
        if (aResult.getParseCode().isError()) {
            return true
        }

        return b.hasMatch(aResult.getSeek(), string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        val aResult = a.parse(seek, string)
        return if (aResult.getParseCode().isError()) {
            -seek
        } else {
            b.noParse(aResult.getSeek(), string)
        }
    }
}