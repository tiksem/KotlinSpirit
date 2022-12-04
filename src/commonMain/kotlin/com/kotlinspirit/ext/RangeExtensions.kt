package com.kotlinspirit.ext

import java.util.StringJoiner

internal operator fun CharRange.minus(range: CharRange): List<CharRange> {
    if (range.first <= this.first && range.last >= this.last) {
        return emptyList()
    }

    return if (range.first > this.first && range.last < this.last) {
        listOf(
            first until range.first, (range.last + 1)..last
        )
    } else if (range.first <= this.first && range.last >= this.first) {
        listOf(
            (range.last + 1)..this.last
        )
    } else if (range.first > this.first && range.last >= this.last && range.first <= this.last) {
        listOf(
            this.first until range.first
        )
    } else {
        listOf(this)
    }
}

internal operator fun CharRange.plus(range: CharRange): List<CharRange> {
    return when {
        range.first <= this.first && range.last >= this.last -> {
            listOf(range)
        }
        this.first <= range.first && this.last >= range.last -> {
            listOf(this)
        }
        range.first <= this.first && range.last <= this.last && range.last >= this.first - 1 -> {
            listOf(range.first..this.last)
        }
        range.first > this.first && range.last >= this.last && range.first <= this.last + 1 -> {
            listOf(this.first..range.last)
        }
        else -> {
            return listOf(this, range)
        }
    }
}

// List should be sorted by first
private fun List<CharRange>.optimizeRanges(): List<CharRange> {
    if (size <= 1) {
        return this
    }

    val result = arrayListOf(this[0])
    var i = 1
    val size = this.size
    while (i < size) {
        result.addAll(result.removeLast() + this[i])
        i++
    }

    return result
}

internal fun List<CharRange>.includeRanges(ranges: List<CharRange>): List<CharRange> {
    return (this + ranges).sortedBy {
        it.first
    }.optimizeRanges()
}

internal fun List<CharRange>.excludeRange(range: CharRange): List<CharRange> {
    val result = ArrayList<CharRange>()
    for (thisRange in this) {
        result.addAll(thisRange - range)
    }

    return result
}

internal fun List<CharRange>.excludeRanges(ranges: List<CharRange>): List<CharRange> {
    if (ranges.isEmpty()) {
        return this
    }

    var list = this
    for (range in ranges) {
        list = list.excludeRange(range)
    }

    return list.sortedBy {
        it.first
    }.optimizeRanges()
}

internal val IntRange.debugString: String
    get() {
        if (this.last == Int.MAX_VALUE) {
            return "$first,max"
        } else {
            return "$first,$last"
        }
    }