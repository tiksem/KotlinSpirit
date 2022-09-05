package com.example.kotlinspirit

import java.lang.IllegalStateException

class FailIfRule<T : Any>(
    private val rule: Rule<T>,
    private val failPredicate: (T) -> Boolean
) : RuleWithDefaultRepeat<T>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        val result = ParseResult<T>()
        rule.parseWithResult(seek, string, result)
        if (result.isError) {
            return result.parseResult
        }

        val data = result.data ?: throw IllegalStateException("rule produces null, without error")
        if (failPredicate(data)) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.FAIL_PREDICATE
            )
        }

        return result.parseResult
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (result.isError) {
            return
        }

        val data = result.data ?: throw IllegalStateException("rule produces null, without error")
        if (failPredicate(data)) {
             result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.FAIL_PREDICATE
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode().isNotError()
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        val noParseResult = rule.noParse(seek, string)
        if (noParseResult >= 0) {
            val result = ParseResult<T>()
            parseWithResult(noParseResult, string, result)
            if (result.isError) {
                throw IllegalStateException("FailIfRule undefined behaviour")
            }
            val data = result.data ?: throw IllegalStateException("rule produces null, without error")
            return if (failPredicate(data)) {
                noParse(result.seek, string)
            } else {
                noParseResult
            }
        } else {
            return noParseResult
        }
    }

    override fun failIf(predicate: (T) -> Boolean): FailIfRule<T> {
        return FailIfRule(rule = rule, failPredicate = {
            failPredicate(it) || predicate(it)
        })
    }

    override fun clone(): RuleWithDefaultRepeat<T> {
        return FailIfRule(rule.clone(), failPredicate)
    }
}