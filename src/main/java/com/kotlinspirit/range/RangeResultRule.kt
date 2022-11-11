package com.kotlinspirit.range

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class RangeResultRuleDefault<T : Any>(
    private val rule: RuleWithDefaultRepeat<T>
) : RuleWithDefaultRepeat<T>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        return rule.parse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {

    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        TODO("Not yet implemented")
    }

    override fun ignoreCallbacks(): Rule<T> {
        TODO("Not yet implemented")
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = TODO("Not yet implemented")

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }

    override fun clone(): RuleWithDefaultRepeat<T> {
        TODO("Not yet implemented")
    }

    override fun debug(name: String?): RuleWithDefaultRepeat<T> {
        TODO("Not yet implemented")
    }
}