package com.kotlinspirit.rangeres

open class ParseRange internal constructor(
    startSeek: Int = -1,
    endSeek: Int = -1
) {
    var startSeek: Int = startSeek
        internal set
    var endSeek: Int = endSeek
        internal set

    internal open fun copy(): ParseRange {
        return ParseRange(startSeek, endSeek)
    }
}

class ParseRangeResult<T: Any> internal constructor(
    data: T? = null,
    startSeek: Int = -1,
    endSeek: Int = -1
) : ParseRange(startSeek, endSeek) {
    var data: T? = data
       internal set

    override fun copy(): ParseRange {
        return ParseRangeResult(data, startSeek, endSeek)
    }
}

fun range(): ParseRange {
    return ParseRange()
}

fun <T : Any> rangeResult(): ParseRangeResult<T> {
    return ParseRangeResult()
}

fun <T : Any> rangeResultList(): MutableList<ParseRangeResult<T>> {
    return ArrayList()
}