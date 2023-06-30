package com.kotlinspirit.custom

import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isNotError
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

abstract class CustomRule<T : Any>(name: String? = null) : RuleWithDefaultRepeat<T>(name) {
    protected fun createComplete(seek: Int): Long {
        return com.kotlinspirit.core.createComplete(seek)
    }

    protected fun createParseResult(seek: Int, parseResult: Int): Long {
        return createStepResult(
            seek = seek,
            parseCode = parseResult
        )
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false
    override val defaultDebugName: String
        get() = "custom"

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode().isNotError()
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return reverseParse(seek, string).getParseCode().isNotError()
    }
}