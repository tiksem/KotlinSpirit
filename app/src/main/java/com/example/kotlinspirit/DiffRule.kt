package com.example.kotlinspirit

private fun internalParse(seek: Int, string: CharSequence, main: Rule<*>, diff: Rule<*>): Long {
    val mainRes = main.parse(seek, string)
    return if (mainRes.getParseCode().isError()) {
        mainRes
    } else {
        val diffRes = diff.parse(seek, string)
        if (diffRes.getParseCode().isError()) {
            mainRes
        } else {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.DIFF_FAILED
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
        if (diffRes.getParseCode().isNotError()) {
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

private fun generateDebugName(main: Rule<*>, diff: Rule<*>): String {
    val mainName = main.debugNameWrapIfNeed
    val diffName = diff.debugNameWrapIfNeed
    return "$mainName - $diffName"
}

open class DiffRuleWithDefaultRepeat<T : Any>(
    protected val main: Rule<T>,
    protected val diff: Rule<*>
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

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override fun debug(name: String?): DiffRuleWithDefaultRepeat<T> {
        val main = main.internalDebug()
        val diff = diff.internalDebug()

        return DebugDiffRuleWithDefaultRepeat(
            name = name ?: generateDebugName(main, diff),
            main = main,
            diff = diff
        )
    }
}

private class DebugDiffRuleWithDefaultRepeat<T : Any>(
    override val name: String,
    main: Rule<T>,
    diff: Rule<*>
) : DiffRuleWithDefaultRepeat<T>(main, diff), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }

    override fun clone(): DiffRuleWithDefaultRepeat<T> {
        return DebugDiffRuleWithDefaultRepeat(name, main.clone(), diff.clone())
    }
}

open class CharDiffRule(
    protected val main: Rule<Char>,
    protected val diff: Rule<*>
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

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override fun clone(): CharDiffRule {
        return CharDiffRule(main.clone(), diff.clone())
    }

    override fun debug(name: String?): CharDiffRule {
        val main = main.internalDebug()
        val diff = diff.internalDebug()

        return DebugCharDiffRule(
            name = name ?: generateDebugName(main, diff),
            main = main,
            diff = diff
        )
    }
}

private class DebugCharDiffRule(
    override val name: String,
    main: Rule<Char>,
    diff: Rule<*>
) : CharDiffRule(main, diff), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }

    override fun clone(): CharDiffRule {
        return DebugCharDiffRule(name, main.clone(), diff.clone())
    }
}

