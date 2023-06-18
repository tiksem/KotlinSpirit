package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.getSeek
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import com.kotlinspirit.repeat.RuleWithDefaultRepeatResult

class StringRuleWrapper(
    private val rule: Rule<*>,
    name: String? = null
) : RuleWithDefaultRepeat<CharSequence>(name) {

    override fun invoke(callback: (CharSequence) -> Unit): RuleWithDefaultRepeatResult<CharSequence> {
        return RuleWithDefaultRepeatResult(this, callback)
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        return rule.parse(seek, string)
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<CharSequence>
    ) {
        val parseResult = rule.parse(seek, string)
        result.parseResult = parseResult
        if (parseResult.getParseCode().isNotError()) {
            result.data = string.subSequence(seek, parseResult.getSeek())
        } else {
            result.data = null
        }
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return rule.reverseParse(seek, string)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        val parseResult = rule.reverseParse(seek, string)
        result.parseResult = parseResult
        if (parseResult.getParseCode().isNotError()) {
            result.data = string.subSequence(parseResult.getSeek() + 1, seek + 1)
        } else {
            result.data = null
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.reverseHasMatch(seek, string)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
    }

    override fun clone(): StringRuleWrapper {
        return StringRuleWrapper(rule = rule.clone(), name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(engine: DebugEngine): DebugRule<CharSequence> {
        return DebugRule(
            rule = StringRuleWrapper(rule.debug(engine), name),
            engine = engine
        )
    }

    override fun name(name: String): StringRuleWrapper {
        return StringRuleWrapper(rule, name)
    }

    override val defaultDebugName: String
        get() = "asString"

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun ignoreCallbacks(): StringRuleWrapper {
        return StringRuleWrapper(rule.ignoreCallbacks())
    }
}
