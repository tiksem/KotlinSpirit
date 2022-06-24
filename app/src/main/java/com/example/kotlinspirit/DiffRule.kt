package com.example.kotlinspirit

class DiffRule<T : Any>(
    private val main: Rule<T>,
    private val diff: Rule<*>
) : RuleWithDefaultRepeat<T>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        val mainRes = main.parse(seek, string)
        return if (mainRes.getParseCode().isError()) {
            mainRes
        } else {
            val diffRes = diff.parse(seek, string)
            if (diffRes.getParseCode().isError()) {
                mainRes
            } else if (diffRes.getSeek() >= mainRes.getSeek()) {
                createStepResult(
                    seek = seek,
                    parseCode = ParseCode.DIFF_FAILED
                )
            } else {
                createStepResult(
                    seek = mainRes.getSeek(),
                    parseCode = ParseCode.COMPLETE
                )
            }
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        main.parseWithResult(seek, string, result)
        val stepResult = result.parseResult
        if (stepResult.getParseCode().isNotError()) {
            val diffRes = diff.parse(seek, string)
            if (diffRes.getParseCode().isNotError() && diffRes.getSeek() >= stepResult.getSeek()) {
                result.parseResult = createStepResult(
                    seek = seek,
                    parseCode = ParseCode.DIFF_FAILED
                )
                return
            }
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return main.hasMatch(seek, string) && !diff.hasMatch(seek, string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        var i = seek
        val length = string.length
        while (true) {
            val diffRes = diff.parse(i, string)
            if (diffRes.getParseCode().isNotError()) {
                i = diffRes.getSeek()
            } else {
                val mainRes = main.noParse(i, string)
                if (mainRes >= 0) {
                    i = mainRes
                } else {
                    return -mainRes
                }
            }

            if (i >= length) {
                return i
            }
        }
    }

    override fun not(): Rule<*> {
        return DiffRule(
            main = diff,
            diff = main
        )
    }
}