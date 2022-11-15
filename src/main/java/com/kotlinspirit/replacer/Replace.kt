package com.kotlinspirit.replacer

import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isError
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.ext.replaceRanges
import com.kotlinspirit.ext.toCharSequence
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult
import java.util.concurrent.ConcurrentHashMap

internal class ReplaceAction(
    val range: ParseRange,
    val replacementProvider: (CharSequence) -> CharSequence
)

internal class ListReplaceAction(
    val ranges: MutableList<out ParseRange>,
    val replacementProvider: (CharSequence, Int) -> CharSequence
)

class ReplaceBuilder internal constructor(
    private val replacements: MutableList<ReplaceAction>,
    private val listReplacements: MutableList<ListReplaceAction>
) {
    fun replace(range: ParseRange, string: CharSequence) {
        replacements.add(ReplaceAction(range) { string })
    }

    fun replace(range: ParseRange, provider: (CharSequence) -> CharSequence) {
        replacements.add(
            ReplaceAction(range) {
                val sub = it.subSequence(range.startSeek, range.endSeek)
                provider(sub)
            }
        )
    }

    fun <T : Any> replace(rangeResult: ParseRangeResult<T>, provider: (T) -> Any) {
        replacements.add(
            ReplaceAction(rangeResult) {
                provider(rangeResult.data!!).toCharSequence()
            }
        )
    }

    fun <T : Any> replaceOptional(rangeResult: ParseRangeResult<T>, provider: (T?) -> Any) {
        replacements.add(
            ReplaceAction(rangeResult) {
                provider(rangeResult.data).toCharSequence()
            }
        )
    }

    fun replace(ranges: MutableList<ParseRange>, string: CharSequence) {
        listReplacements.add(ListReplaceAction(ranges) { _, _ -> string })
    }

    fun <T : Any> replace(ranges: MutableList<ParseRangeResult<T>>, provider: (T) -> Any) {
        listReplacements.add(ListReplaceAction(ranges) { string, index ->
            provider(ranges[index].data!!).toCharSequence()
        })
    }
}

abstract class BaseReplace {
    internal abstract val replacements: List<ReplaceAction>
    internal abstract val listReplacements: List<ListReplaceAction>

    internal fun appendListReplacements(
        string: CharSequence,
        ranges: MutableList<ParseRange>,
        replacements: MutableList<CharSequence>
    ) {
        for (l in listReplacements) {
            l.ranges.indices.mapTo(replacements) {
                l.replacementProvider(string, it)
            }

            l.ranges.mapTo(ranges) {
                it.copy()
            }
        }
    }
}

class Replace(
    internal val rule: Rule<*>,
    builder: ReplaceBuilder.() -> Unit
) : BaseReplace() {
    override val replacements: List<ReplaceAction>
    override val listReplacements: List<ListReplaceAction>

    init {
        val replacements = ArrayList<ReplaceAction>()
        val listReplacements = ArrayList<ListReplaceAction>()
        ReplaceBuilder(replacements, listReplacements).builder()
        this.replacements = replacements
        this.listReplacements = listReplacements
    }
}

class Replacer(
    private val provider: () -> Replace
) {
    private val map = ConcurrentHashMap<Long, Replace>()

    private fun get(string: CharSequence): Replace {
        return map.getOrPut(Thread.currentThread().id) {
            provider().also {
                if (it.rule is DebugRule) {
                    DebugEngine.startDebugSession(string)
                }
            }
        }.apply {
            listReplacements.forEach {
                it.ranges.clear()
            }
        }
    }

    fun replaceIfMatch(seek: Int, string: CharSequence): CharSequence {
        val data = get(string)
        val result = data.rule.parse(seek, string)
        if (result.getParseCode().isError()) {
            return string
        }

        val ranges = data.replacements.map {
            it.range
        }

        val replacements = data.replacements.map {
            it.replacementProvider(string)
        }

        return string.replaceRanges(ranges, replacements)
    }

    fun replaceFirst(string: CharSequence): CharSequence {
        val data = get(string)
        data.rule.findFirstSuccessfulSeek(string) { _, _ ->
            var ranges = data.replacements.map {
                it.range
            }

            var replacements = data.replacements.map {
                it.replacementProvider(string)
            }

            if (data.listReplacements.isNotEmpty()) {
                val mutableRanges = ranges.toMutableList()
                val mutableReplacements = replacements.toMutableList()

                data.appendListReplacements(string, mutableRanges, mutableReplacements)

                ranges = mutableRanges
                replacements = mutableReplacements
            }

            return string.replaceRanges(ranges, replacements)
        }

        return string
    }

    fun replaceAll(string: CharSequence): CharSequence {
        val data = get(string)

        val ranges = arrayListOf<ParseRange>()
        val replacements = arrayListOf<CharSequence>()

        data.rule.findSuccessfulRanges(string) { _, _ ->
            data.replacements.map {
                it.range.copy()
            }.also {
                ranges.addAll(it)
            }

            data.replacements.map {
                it.replacementProvider(string)
            }.also {
                replacements.addAll(it)
            }

            data.appendListReplacements(string, ranges, replacements)
            data.listReplacements.forEach {
                it.ranges.clear()
            }
        }

        if (ranges.isEmpty()) {
            return string
        }

        return string.replaceRanges(ranges, replacements)
    }
}