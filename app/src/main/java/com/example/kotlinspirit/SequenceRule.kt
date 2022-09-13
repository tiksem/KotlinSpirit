package com.example.kotlinspirit

open class SequenceRule(
    protected val a: Rule<*>,
    protected val b: Rule<*>
) : RuleWithDefaultRepeat<CharSequence>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        val aResult = a.parse(seek, string)
        if (aResult.getParseCode().isError()) {
            return aResult
        }

        return b.parse(aResult.getSeek(), string)
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
        return if (aResult.getParseCode().isError()) {
            false
        } else {
            b.hasMatch(aResult.getSeek(), string)
        }
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        val aNoParseResult = a.noParse(seek, string)
        return if (aNoParseResult < 0) {
            val aParseResult = a.parse(seek, string)
            b.noParse(aParseResult.getSeek(), string)
        } else {
            aNoParseResult
        }
    }

    override fun clone(): SequenceRule {
        return SequenceRule(a.clone(), b.clone())
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override fun debug(name: String?): SequenceRule {
        val a = a.internalDebug()
        val b = b.internalDebug()
        val aName = if (a is SequenceRule) a.debugName else a.debugNameWrapIfNeed
        val bName = if (b is SequenceRule) b.debugName else b.debugNameWrapIfNeed
        return DebugSequenceRule(
            name = name ?: "$aName + $bName",
            a, b
        )
    }
}

private class DebugSequenceRule(
    override val name: String,
    a: Rule<*>,
    b: Rule<*>
): SequenceRule(a, b), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<CharSequence>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }

    override fun clone(): SequenceRule {
        return DebugSequenceRule(name, a.clone(), b.clone())
    }
}