package com.kotlinspirit.expressive

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.ext.debugString
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

class SplitRule<T : Any>(
    private val r: Rule<T>,
    private val divider: Rule<*>,
    private val range: IntRange,
    name: String? = null
) : RuleWithDefaultRepeat<List<T>>(name) {
    init {
        if (range.first < 0) {
            throw IllegalStateException("negative range.first value")
        }
    }

    private inline fun baseParse(
        seek: Int,
        string: CharSequence,
        parser: (rule: Rule<*>, seek: Int, string: CharSequence) -> Long
    ): Long {
        var numberOfSplitItems = 0
        val max = range.last
        var i = seek
        var seekAfterRule = i
        while (numberOfSplitItems < max) {
            val res = parser(r, i, string)
            if (res.getParseCode().isError()) {
                i = seekAfterRule
                break
            }
            seekAfterRule = res.getSeek()
            numberOfSplitItems++

            val dividerRes = parser(divider, seekAfterRule, string)
            if (dividerRes.getParseCode().isError()) {
                i = seekAfterRule
                break
            }
            i = dividerRes.getSeek()
        }

        return if (numberOfSplitItems < range.first) {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.SPLIT_NOT_ENOUGH_DATA
            )
        } else {
            createComplete(seek = i)
        }
    }

    private inline fun baseParseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<List<T>>,
        parser: (rule: Rule<*>, seek: Int, string: CharSequence) -> Long,
        parserWithResult: (rule: Rule<T>, seek: Int, string: CharSequence, result: ParseResult<T>) -> Unit
    ) {
        val list = ArrayList<T>()
        val max = range.last
        var i = seek
        var seekAfterRule = i
        val itemResult = ParseResult<T>()
        while (list.size < max) {
            parserWithResult(r, i, string, itemResult)
            if (itemResult.isError) {
                i = seekAfterRule
                break
            }
            seekAfterRule = itemResult.endSeek
            list.add(itemResult.data ?: throw IllegalStateException("item result should not be null"))

            val dividerRes = parser(divider, seekAfterRule, string)
            if (dividerRes.getParseCode().isError()) {
                i = seekAfterRule
                break
            }
            i = dividerRes.getSeek()
        }
        result.data = list

        result.parseResult = if (list.size < range.first) {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.SPLIT_NOT_ENOUGH_DATA
            )
        } else {
            createComplete(seek = i)
        }
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        return baseParse(
            seek = seek,
            string = string,
            parser = { rule, s, str ->
                rule.parse(s, str)
            }
        )
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<List<T>>) {
        baseParseWithResult(
            seek = seek,
            string = string,
            result = result,
            parser = { rule, s, str ->
                rule.parse(s, str)
            },
            parserWithResult = { rule, s, str, r ->
                rule.parseWithResult(
                    seek = s,
                    string = string,
                    result = r
                )
            }
        )
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode().isNotError()
    }

    override fun reverseParse(seek: Int, string: CharSequence): Long {
        return baseParse(
            seek = seek,
            string = string,
            parser = { rule, s, str ->
                rule.reverseParse(s, str)
            }
        )
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<List<T>>) {
        baseParseWithResult(
            seek = seek,
            string = string,
            result = result,
            parser = { rule, s, str ->
                rule.reverseParse(s, str)
            },
            parserWithResult = { rule, s, str, r ->
                rule.reverseParseWithResult(
                    seek = s,
                    string = string,
                    result = r
                )
            }
        )
        result.data = result.data?.asReversed()
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return reverseParse(seek, string).getParseCode().isNotError()
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "${r.wrappedName}.split(${divider.debugName} , ${range.debugString})"

    override fun clone(): SplitRule<T> {
        return SplitRule(r.clone(), divider.clone(), range, name)
    }

    override fun debug(engine: DebugEngine): DebugRule<List<T>> {
        return DebugRule(
            rule = SplitRule(r.debug(engine), divider.debug(engine), range),
            engine = engine
        )
    }

    override fun isThreadSafe(): Boolean {
        return r.isThreadSafe() && divider.isThreadSafe()
    }

    override fun name(name: String): SplitRule<T> {
        return SplitRule(r, divider, range, name)
    }
}
