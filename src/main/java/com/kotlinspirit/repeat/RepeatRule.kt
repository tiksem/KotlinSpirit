package com.kotlinspirit.repeat

import com.kotlinspirit.core.*
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.ext.debugString

class RepeatRule<T : Any>(
    private val rule: Rule<T>,
    private val range: IntRange,
    name: String? = null
) : RuleWithDefaultRepeat<List<T>>(name) {
    private inline fun baseParse(
        seek: Int,
        parser: (seek: Int) -> Long
    ): Long {
        var i = seek
        var resultsCount = 0

        while (resultsCount < range.last) {
            val seekBefore = i
            val ruleRes = parser(i)
            if (ruleRes.getParseCode().isError()) {
                return if (resultsCount < range.first) {
                    ruleRes
                } else {
                    createComplete(seekBefore)
                }
            } else {
                i = ruleRes.getSeek()
                if (i == seekBefore) {
                    return if (resultsCount < range.first) {
                        ruleRes
                    } else {
                        createComplete(seekBefore)
                    }
                }
                resultsCount++
            }
        }

        return createComplete(i)
    }

    private inline fun baseParseWithResult(
        seek: Int,
        result: ParseResult<List<T>>,
        parser: (seek: Int, r: ParseResult<T>) -> Unit
    ) {
        var i = seek
        val list = ArrayList<T>(range.first)
        val itemResult = ParseResult<T>()
        while (list.size < range.last) {
            val seekBefore = i
            parser(i, itemResult)
            val stepResult = itemResult.parseResult
            if (stepResult.getParseCode().isError()) {
                if (list.size >= range.first) {
                    result.data = list
                    result.parseResult = createComplete(seekBefore)
                } else {
                    result.parseResult = stepResult
                    result.data = null
                }
                return
            } else {
                i = stepResult.getSeek()
                if (i == seekBefore) {
                    break
                }
                list.add(itemResult.data ?: continue)
            }
        }

        if (list.size >= range.first) {
            result.data = list
            result.parseResult = createComplete(i)
        } else {
            result.parseResult = createStepResult(
                seek = i,
                parseCode = ParseCode.EOF
            )
        }
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        return baseParse(
            seek = seek,
            parser = {
                rule.parse(seek = it, string = string)
            }
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<List<T>>) {
        baseParseWithResult(seek, result, parser = { s, r ->
            rule.parseWithResult(s, string, r)
        })
    }

    private inline fun baseHasMatch(
        seek: Int,
        parser: (seek: Int) -> Long,
        hasMatch: (seek: Int) -> Boolean
    ): Boolean {
        if (range.first <= 0) {
            return true
        }

        var i = seek
        repeat (range.first - 1) {
            val r = parser(i)
            if (r.getParseCode().isError()) {
                return false
            }
            val seekBefore = i
            i = r.getSeek()
            if (seekBefore != i) {
                return false
            }
        }

        return hasMatch(i)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return baseHasMatch(
            seek = seek,
            parser = {
                rule.parse(seek = it, string = string)
            },
            hasMatch = {
                rule.hasMatch(seek = it, string = string)
            }
        )
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return baseParse(
            seek = seek,
            parser = {
                rule.reverseParse(seek = it, string = string)
            }
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<List<T>>) {
        baseParseWithResult(seek, result, parser = { s, r ->
            rule.reverseParseWithResult(s, string, r)
        })
        result.data = result.data?.asReversed()
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return baseHasMatch(
            seek = seek,
            parser = {
                rule.reverseParse(seek = it, string = string)
            },
            hasMatch = {
                rule.reverseHasMatch(seek = it, string = string)
            }
        )
    }

    override fun clone(): RepeatRule<T> {
        return RepeatRule(rule.clone(), range, name)
    }

    override fun debug(engine: DebugEngine): DebugRule<List<T>> {
        return DebugRule(
            rule = RepeatRule(rule.debug(engine), range, name),
            engine = engine
        )
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun name(name: String): RepeatRule<T> {
        return RepeatRule(rule, range, name)
    }

    override val defaultDebugName: String
        get() = "${rule.wrappedName}.repeat(${range.debugString})"

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }
}