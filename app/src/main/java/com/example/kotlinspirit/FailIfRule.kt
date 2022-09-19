package com.example.kotlinspirit

import java.lang.IllegalStateException

open class FailIfRule<T : Any>(
    protected val rule: Rule<T>,
    protected val failPredicate: (T) -> Boolean
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
        if (noParseResult < 0) {
            val result = ParseResult<T>()
            rule.parseWithResult(seek, string, result)
            if (result.isError) {
                throw IllegalStateException("FailIfRule undefined behaviour")
            }
            val data = result.data ?: throw IllegalStateException("rule produces null, without error")
            return if (failPredicate(data)) {
                val r = noParse(result.seek, string)
                if (r < 0) {
                    result.seek
                } else {
                    r
                }
            } else {
                noParseResult
            }
        } else {
            return if (noParseResult == seek) {
                seek
            } else {
                val r = noParse(noParseResult, string)
                if (r < 0) {
                    noParseResult
                } else {
                    r
                }
            }
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

    override val debugNameShouldBeWrapped: Boolean
        get() = rule.debugNameShouldBeWrapped

    override fun debug(name: String?): FailIfRule<T> {
        return DebugFailIfRule(name ?: "failIf", rule.internalDebug(), failPredicate)
    }
}

private class DebugFailIfRule<T : Any>(
    override val name: String,
    rule: Rule<T>,
    failPredicate: (T) -> Boolean
) : FailIfRule<T>(rule, failPredicate), DebugRule {
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

    override fun clone(): RuleWithDefaultRepeat<T> {
        return DebugFailIfRule(name, rule.clone(), failPredicate)
    }
}