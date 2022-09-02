package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.str

private fun internalParse(seek: Int, string: CharSequence, main: Rule<*>, diff: Rule<*>): Long {
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

private fun <T : Any> internalParseWithResult(
    seek: Int, string: CharSequence, result: ParseResult<T>, main: Rule<T>, diff: Rule<*>
) {
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

private fun internalHasMatch(
    seek: Int, string: CharSequence, main: Rule<*>, diff: Rule<*>
): Boolean {
    return main.hasMatch(seek, string) && !diff.hasMatch(seek, string)
}

private fun internalNoParse(seek: Int, string: CharSequence, main: Rule<*>, diff: Rule<*>): Int {
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
                return -mainRes - 1
            }
        }

        if (i >= length) {
            return i
        }
    }
}

class DiffRuleWithDefaultRepeat<T : Any>(
    private val main: Rule<T>,
    private val diff: Rule<*>
) : RuleWithDefaultRepeat<T>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        return internalParse(seek, string, main, diff)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        return internalParseWithResult(seek, string, result, main, diff)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return internalHasMatch(seek, string, main, diff)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return internalNoParse(seek, string, main, diff)
    }

    override fun clone(): DiffRuleWithDefaultRepeat<T> {
        return DiffRuleWithDefaultRepeat(main.clone(), diff.clone())
    }
}

class CharDiffRule(
    private val main: Rule<Char>,
    private val diff: Rule<*>
) : CharRule() {
    override fun parse(seek: Int, string: CharSequence): Long {
        return internalParse(seek, string, main, diff)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        return internalParseWithResult(seek, string, result, main, diff)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return internalHasMatch(seek, string, main, diff)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return internalNoParse(seek, string, main, diff)
    }

    override fun clone(): CharDiffRule {
        return CharDiffRule(main.clone(), diff.clone())
    }
}

