package com.kotlinspirit.hook

interface ParseRange {
    val startSeek: Int
    val endSeek: Int
}

interface ParseRangeResult<T : Any> {
    val startSeek: Int
    val endSeek: Int
    val result: T?
}

interface RangeHook {
    fun submit(startSeek: Int, endSeek: Int)
}

interface SingleRangeHook : RangeHook, ParseRange {
}

interface RangeListHook : RangeHook {
    val ranges: List<ParseRange>
    fun clear()
}

interface RangeResultHook<T : Any> {
    fun submit(startSeek: Int, endSeek: Int, result: T?)
}

interface SingleRangeResultHook<T : Any> : RangeResultHook<T>, ParseRangeResult<T>

internal class SingleRangeHookImpl : SingleRangeHook {
    override var startSeek: Int = -1
        private set
    override var endSeek: Int = -1
        private set

    override fun submit(startSeek: Int, endSeek: Int) {
        this.startSeek = startSeek
        this.endSeek = endSeek
    }
}

class SingleRangeResultHookImpl<T : Any> : SingleRangeResultHook<T> {
    override var startSeek: Int = -1
        private set
    override var endSeek: Int = -1
        private set
    override var result: T? = null
        private set

    override fun submit(startSeek: Int, endSeek: Int, result: T?) {
        this.startSeek = startSeek
        this.endSeek = endSeek
        this.result = result
    }
}

internal class RangeListHookImpl : RangeListHook {
    private data class R(
        override val startSeek: Int,
        override val endSeek: Int
    ) : ParseRange

    private val _ranges = ArrayList<ParseRange>()

    override fun submit(startSeek: Int, endSeek: Int) {
        _ranges.add(R(startSeek, endSeek))
    }

    override val ranges: List<ParseRange>
        get() = _ranges

    override fun clear() {
        _ranges.clear()
    }
}

interface RangeResultListHook<T : Any> : RangeResultHook<T> {
    val ranges: List<ParseRangeResult<T>>
    fun clear()
}

internal class RangeResultListHookImpl<T : Any> : RangeResultListHook<T> {
    private data class R<T : Any>(
        override val startSeek: Int,
        override val endSeek: Int,
        override val result: T?
    ) : ParseRangeResult<T>

    private val _ranges = ArrayList<ParseRangeResult<T>>()

    override val ranges: List<ParseRangeResult<T>>
        get() = _ranges

    override fun submit(startSeek: Int, endSeek: Int, result: T?) {
        _ranges.add(R(startSeek, endSeek, result))
    }

    override fun clear() {
        _ranges.clear()
    }
}

fun range(): SingleRangeHook {
    return SingleRangeHookImpl()
}

fun rangeList(): RangeListHook {
    return RangeListHookImpl()
}

fun <T : Any> rangeResult(): SingleRangeResultHook<T> {
    return SingleRangeResultHookImpl()
}

fun <T : Any> rangeResultList(): RangeResultListHook<T> {
    return RangeResultListHookImpl<T>()
}