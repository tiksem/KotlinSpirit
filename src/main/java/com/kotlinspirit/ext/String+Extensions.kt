package com.kotlinspirit.ext

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

private fun checkRanges(ranges: List<IntRange>) {
    val size = ranges.size
    if (size in 0..1) {
        return
    }

    var i = 0
    do {
        val curr = ranges[i]
        val next = ranges[i + 1]
        if (curr.last > next.first) {
            throw IllegalStateException("Invalid ranges passed, ranges should not intersect")
        }

        i++
    } while (i < size - 1)
}

fun CharSequence.replaceRanges(ranges: List<IntRange>, replacement: CharSequence): CharSequence {
    if (ranges.isEmpty()) {
        return this
    }

    if (ranges.size == 1) {
        return replaceRange(ranges.first(), replacement)
    }

    checkRanges(ranges)

    val result = StringBuilder()
    result.appendRange(this, 0, ranges.first().first)
    result.append(replacement)

    var i = 0
    do {
        val first = ranges[i].last + 1
        val last = ranges[i + 1].first
        result.appendRange(this, first, last)
        result.append(replacement)
        i++
    } while (i < ranges.size - 1)

    result.appendRange(this, ranges.last().last + 1, length)

    return result
}
