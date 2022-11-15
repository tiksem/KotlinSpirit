package com.kotlinspirit.ext

import com.kotlinspirit.rangeres.ParseRange

fun String.quote(start: Char, end: Char): String {
    return "$start$this$end"
}

fun String.quoteIf(start: Char, end: Char, condition: Boolean): String {
    return if (condition) {
        quote(start, end)
    } else {
        this
    }
}

fun String.containsAny(characters: String): Boolean {
    return characters.any {
        this.contains(it)
    }
}

fun StringBuilder.appendRange(string: CharSequence, range: IntRange) {
    appendRange(string, range.first, range.last - 1)
}

private fun checkRanges(ranges: List<ParseRange>) {
    val size = ranges.size
    if (size in 0..1) {
        return
    }

    var i = 0
    do {
        val curr = ranges[i]
        val next = ranges[i + 1]
        if (curr.endSeek > next.startSeek) {
            throw IllegalStateException("Invalid ranges passed, ranges should not intersect")
        }

        i++
    } while (i < size - 1)
}

internal fun CharSequence.replaceRanges(ranges: List<ParseRange>, replacement: CharSequence): CharSequence {
    if (ranges.isEmpty()) {
        return this
    }

    val firstRange = ranges.first()
    if (ranges.size == 1) {
        return replaceRange(firstRange.startSeek, firstRange.endSeek, replacement)
    }

    checkRanges(ranges)

    val result = StringBuilder()
    result.appendRange(this, 0, firstRange.startSeek)
    result.append(replacement)

    var i = 0
    do {
        val first = ranges[i].endSeek
        val last = ranges[i + 1].startSeek
        result.appendRange(this, first, last)
        result.append(replacement)
        i++
    } while (i < ranges.size - 1)

    result.appendRange(this, ranges.last().endSeek, length)

    return result
}

internal fun CharSequence.replaceRanges(ranges: List<ParseRange>, replacements: List<CharSequence>): CharSequence {
    assert(ranges.size == replacements.size)
    if (replacements.isEmpty()) {
        return this
    }

    val firstRange = ranges.first()
    if (ranges.size == 1) {
        return replaceRange(firstRange.startSeek, firstRange.endSeek, replacements.first())
    }

    checkRanges(ranges)

    val result = StringBuilder()
    result.appendRange(this, 0, firstRange.startSeek)
    result.append(replacements.first())

    var i = 0
    do {
        val first = ranges[i].endSeek
        val last = ranges[i + 1].startSeek
        result.appendRange(this, first, last)
        result.append(replacements[i + 1])
        i++
    } while (i < ranges.size - 1)

    result.appendRange(this, ranges.last().endSeek, length)

    return result
}

fun Any.toCharSequence(): CharSequence {
    if (this is CharSequence) {
        return this
    }

    return toString()
}