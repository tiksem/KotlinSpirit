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
            seekAfterRule = itemResult.endSeek
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

    override fun ignoreCallbacks(): SplitRule<T> {
        return SplitRule(r.ignoreCallbacks(), divider.ignoreCallbacks(), range)
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

    override fun getPrefixMaxLength(): Int {
        val rangeLength = range.last - range.first
        var sum = r.getPrefixMaxLength() * rangeLength.toLong()
        val dividersMaxCount = rangeLength - 1
        if (dividersMaxCount >= 0) {
            sum += divider.getPrefixMaxLength() * dividersMaxCount.toLong()
        }

        return sum.coerceAtMost(MAX_PREFIX_LENGTH.toLong()).toInt()
    }

    override fun isPrefixFixedLength(): Boolean {
        return when (range.last - range.first) {
            0 -> true
            1 -> r.isPrefixFixedLength()
            else -> false
        }
    }

    override fun name(name: String): SplitRule<T> {
        return SplitRule(r, divider, range, name)
    }
}
