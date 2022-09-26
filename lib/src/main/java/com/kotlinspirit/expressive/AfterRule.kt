package com.kotlinspirit.expressive

import com.kotlinspirit.core.*
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isError
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class AfterStringRule<T : Any>(
    private val rule: Rule<T>,
    private val string: String
): RuleWithDefaultRepeat<T>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        val result = rule.parse(seek, string)
        if (result.getParseCode().isError()) {
            return result
        }

        val self = this.string
        if (seek < self.length || !string.subSequence(seek - self.length, seek).startsWith(self)) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.AFTER_FAILED
            )
        }

        return result
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (result.isError) {
            return
        }

        val self = this.string
        if (seek < self.length || !string.subSequence(seek - self.length, seek).startsWith(self)) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.AFTER_FAILED
            )
            result.data = null
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
                && seek >= this.string.length
                && string.subSequence(seek - this.string.length, seek).startsWith(this.string)
    }

    override fun ignoreCallbacks(): Rule<T> {
        return AfterStringRule(rule.ignoreCallbacks(), string)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun clone(): AfterStringRule<T> {
        return AfterStringRule(rule.clone(), string)
    }

    override fun debug(name: String?): RuleWithDefaultRepeat<T> {
        return this
    }
}