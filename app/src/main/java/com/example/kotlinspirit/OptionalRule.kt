package com.example.kotlinspirit

open class OptionalRule<T : Any>(
    protected val rule: Rule<T>
) : RuleWithDefaultRepeat<T>() {

    override fun parse(seek: Int, string: CharSequence): Long {
        return createStepResult(
            seek = rule.parse(seek, string).getSeek(),
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<T>
    ) {
        result.data = null
        val ruleRes = rule.parse(seek, string)
        result.parseResult = createStepResult(
            seek = ruleRes.getSeek(),
            parseCode = ParseCode.COMPLETE
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return -seek - 1
    }

    override fun clone(): OptionalRule<T> {
        return OptionalRule(rule)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): OptionalRule<T> {
        return DebugOptionalRule(name ?: "optional(${rule.debugName})", rule.internalDebug())
    }
}

private class DebugOptionalRule<T : Any>(
    override val name: String,
    rule: Rule<T>
): OptionalRule<T>(rule), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<T>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}

open class OptionalCharRule(rule: CharRule) : OptionalRule<Char>(rule) {
    override fun clone(): OptionalCharRule {
        return OptionalCharRule((rule as CharRule).clone())
    }

    override fun debug(name: String?): OptionalCharRule {
        val debug = rule.internalDebug()
        return DebugOptionalCharRule(name ?: "optional(${debug.debugName})",
            debug as CharRule)
    }
}

private class DebugOptionalCharRule(
    override val name: String,
    rule: CharRule
): OptionalCharRule(rule), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<Char>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}

