package com.kotlinspirit.expressive

import com.kotlinspirit.core.*
import com.kotlinspirit.core.createComplete
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

open class SplitRule<T : Any>(
    protected val r: Rule<T>,
    protected val divider: Rule<*>,
    protected val range: IntRange
) : RuleWithDefaultRepeat<List<T>>() {
    init {
        if (range.first < 0) {
            throw IllegalStateException("negative range.first value")
        }
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        var numberOfSplitItems = 0
        val max = range.last
        var i = seek
        var seekAfterRule = i
        while (numberOfSplitItems < max) {
            val res = r.parse(i, string)
            if (res.getParseCode().isError()) {
                i = seekAfterRule
                break
            }
            seekAfterRule = res.getSeek()
            numberOfSplitItems++

            val dividerRes = divider.parse(seekAfterRule, string)
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

    private fun parseSaveSeekOnError(seek: Int, string: CharSequence): Long {
        var numberOfSplitItems = 0
        val max = range.last
        var i = seek
        var seekAfterRule = i
        while (numberOfSplitItems < max) {
            val res = r.parse(i, string)
            if (res.getParseCode().isError()) {
                i = seekAfterRule
                break
            }
            seekAfterRule = res.getSeek()
            numberOfSplitItems++

            val dividerRes = divider.parse(seekAfterRule, string)
            if (dividerRes.getParseCode().isError()) {
                i = seekAfterRule
                break
            }
            i = dividerRes.getSeek()
        }

        return if (numberOfSplitItems < range.first) {
            createStepResult(
                seek = seekAfterRule,
                parseCode = ParseCode.SPLIT_NOT_ENOUGH_DATA
            )
        } else {
            createComplete(seek = i)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<List<T>>) {
        val list = ArrayList<T>()
        val max = range.last
        var i = seek
        var seekAfterRule = i
        val itemResult = ParseResult<T>()
        while (list.size < max) {
            r.parseWithResult(i, string, itemResult)
            if (itemResult.isError) {
                i = seekAfterRule
                break
            }
            seekAfterRule = itemResult.seek
            list.add(itemResult.data ?: throw IllegalStateException("item result should not be null"))

            val dividerRes = divider.parse(seekAfterRule, string)
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

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return parse(seek, string).getParseCode().isNotError()
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        if (range.first == 0) {
            return -seek-1
        }

        val noParse = r.noParse(seek, string)
        if (noParse < 0) {
            return noParse
        }

        val res = parseSaveSeekOnError(seek, string)
        if (res.getParseCode().isError()) {
            val noRes = noParse(res.getSeek(), string)
            return if (noRes < 0) {
                res.getSeek()
            } else {
                noRes
            }
        } else {
            return noParse
        }
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun clone(): SplitRule<T> {
        return SplitRule(r.clone(), divider.clone(), range)
    }

    override fun debug(name: String?): SplitRule<T> {
        val r = r.internalDebug()
        val divider = divider.internalDebug()
        return DebugSplitRule(
            name = name ?: "${r.debugNameWrapIfNeed}.split(${divider.debugName} , $range)",
            r, divider, range
        )
    }
}

private class DebugSplitRule<T : Any>(
    override val name: String,
    r: Rule<T>,
    divider: Rule<*>,
    range: IntRange
) : SplitRule<T>(r, divider, range), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<List<T>>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }

    override fun clone(): SplitRule<T> {
        return DebugSplitRule(name, r.clone(), divider.clone(), range)
    }
}

