package com.kotlinspirit.ext

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.eof
import com.kotlinspirit.core.Rules.group
import com.kotlinspirit.rangeres.ParseRange
import com.kotlinspirit.rangeres.ParseRangeResult

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

fun CharSequence.matches(rule: Rule<*>): Boolean {
    return rule.parse(0, this).let {
        it.isComplete && it.seek == length
    }
}

fun CharSequence.indexOf(rule: Rule<*>): Int? {
    rule.findFirstSuccessfulSeek(this) { start, _ ->
        return start
    }

    return null
}

fun CharSequence.lastIndexOfShortestMatch(rule: Rule<*>): Int? {
    rule.findLastSuccessfulSeek(this) { start, _ ->
        return start
    }

    return null
}

fun CharSequence.lastIndexOfLongestMatch(rule: Rule<*>): Int? {
    rule.findLastSuccessfulSeek(this) { start, end ->
        var seek = start - 1
        while (seek >= 0) {
            val parseResult = rule.parse(seek, this)
            if (parseResult.isError) {
                break
            }
            val endSeek = parseResult.seek
            if (endSeek < end) {
                break
            }
            --seek
        }

        return seek + 1
    }

    return null
}

fun CharSequence.findFirstRange(rule: Rule<*>): ParseRange? {
    rule.findFirstSuccessfulSeek(this) { start, end ->
        return ParseRange(start, end)
    }

    return null
}

fun <T : Any> CharSequence.findAll(rule: Rule<T>): List<T> {
    val result = ArrayList<T>()
    rule.findSuccessfulResults(this) { start, end, r ->
        result.add(r)
    }

    return result
}

fun CharSequence.findAllRanges(rule: Rule<*>): List<ParseRange> {
    val result = ArrayList<ParseRange>()
    rule.findSuccessfulRanges(this) { start, end ->
        result.add(ParseRange(start, end))
    }

    return result
}

fun <T : Any> CharSequence.findFirst(rule: Rule<T>): T? {
    rule.findFirstSuccessfulResult(this) { _, result ->
        return result.data
    }

    return null
}

fun <T : Any> CharSequence.findFirstResult(rule: Rule<T>): ParseRangeResult<T>? {
    rule.findFirstSuccessfulResult(this) { start, result ->
        return ParseRangeResult(data = result.data, startSeek = start, endSeek = result.endSeek)
    }

    return null
}

fun CharSequence.replaceFirst(rule: Rule<*>, replacement: CharSequence): CharSequence {
    rule.findFirstSuccessfulSeek(this) { start, end ->
        return replaceRange(start until end, replacement)
    }

    return this
}

fun CharSequence.replaceAll(rule: Rule<*>, replacement: CharSequence): CharSequence {
    val ranges = ArrayList<ParseRange>()

    rule.findSuccessfulRanges(this) { start, end ->
        val range = ParseRange(start, end)
        ranges.add(range)
    }

    return replaceRanges(ranges, replacement)
}

fun <T : Any> CharSequence.replaceFirst(rule: Rule<T>, replacementProvider: (T) -> Any): CharSequence {
    rule.findFirstSuccessfulResult(this) { start, result ->
        return replaceRange(
            range = start until result.endSeek,
            replacement = replacementProvider(result.data!!).toCharSequence()
        )
    }

    return this
}

fun <T : Any> CharSequence.replaceAll(rule: Rule<T>, replacementProvider: (T) -> Any): CharSequence {
    val ranges = ArrayList<ParseRange>()
    val replacements = ArrayList<CharSequence>()

    rule.findSuccessfulResults(this) { start, end, value ->
        val range = ParseRange(start, end)
        ranges.add(range)
        replacements.add(replacementProvider(value).toCharSequence())
    }

    return replaceRanges(ranges, replacements)
}

fun <T : Any> CharSequence.findAllResults(rule: Rule<T>): List<ParseRangeResult<T>> {
    val result = ArrayList<ParseRangeResult<T>>()
    rule.findSuccessfulResults(this) { start, end, value ->
        result.add(ParseRangeResult(data = value, startSeek = start, endSeek = end))
    }

    return result
}

fun <T : Any> CharSequence.replaceFirstOrNull(rule: Rule<T>, replacement: CharSequence): CharSequence? {
    rule.findFirstSuccessfulSeek(this) { start, end ->
        return replaceRange(start until end, replacement)
    }

    return null
}

fun <T : Any> CharSequence.replaceFirstOrNull(rule: Rule<T>, replacementProvider: (T) -> CharSequence): CharSequence? {
    rule.findFirstSuccessfulResult(this) { start, result ->
        return replaceRange(
            range = start until result.endSeek,
            replacement = replacementProvider(result.data!!)
        )
    }

    return null
}

fun CharSequence.startsWith(rule: Rule<*>): Boolean {
    return rule.hasMatch(0, this)
}

fun CharSequence.endsWith(rule: Rule<*>): Boolean {
    if (rule.hasMatch(length, this)) {
        return true
    }

    var seek = length - 1
    while (seek >= 0) {
        val parseResult = rule.parse(seek, this)
        if (parseResult.isComplete && parseResult.seek == length) {
            return true
        }
        --seek
    }

    return false
}

fun <T : Any> CharSequence.parseWithResult(rule: Rule<T>): T? {
    val result = ParseResult<T>()
    rule.parseWithResult(0, this, result)
    if (result.isError) {
        return null
    }

    return result.data
}

fun <T : Any> CharSequence.parsePrefixOrThrow(rule: Rule<T>): T {
    val result = ParseResult<T>()
    rule.parseWithResult(0, this, result)
    if (result.isError) {
        throw ParseException(result.parseResult, this)
    }

    return result.data!!
}

fun <T : Any> CharSequence.parseWhole(rule: Rule<T>): T? {
    val result = ParseResult<T>()
    rule.parseWithResult(0, this, result)
    if (result.isError) {
        return null
    }

    if (result.endSeek != length) {
        return null
    }

    return result.data
}

fun <T : Any> CharSequence.parseWholeOrThrow(rule: Rule<T>): T {
    val result = ParseResult<T>()
    group(rule.asResult() + eof).parseWithResult(0, this, result)
    if (result.isError) {
        throw ParseException(result.parseResult, this)
    }

    return result.data!!
}

fun CharSequence.parse(rule: Rule<*>): Int? {
    return rule.parse(0, this).let {
        if (it.isComplete) {
            it.seek
        } else {
            null
        }
    }
}

fun CharSequence.parseOrThrow(rule: Rule<*>): Int {
    return rule.parse(0, this).let {
        if (it.isComplete) {
            it.seek
        } else {
            throw ParseException(it, this)
        }
    }
}

fun CharSequence.split(rule: Rule<*>): List<CharSequence> {
    val ranges = findAllRanges(rule)
    if (ranges.isEmpty()) {
        return listOf(this)
    }

    var begin = 0
    val result = ArrayList<CharSequence>(ranges.size + 1)

    for (range in ranges) {
        result.add(subSequence(begin, range.startSeek))
        begin = range.endSeek
    }

    result.add(subSequence(begin, length))
    return result
}

fun CharSequence.count(rule: Rule<*>): Int {
    var count = 0
    var i = 0
    while (i < length) {
        val rRes = rule.parse(i, this)
        if (rRes.isComplete) {
            val seek = rRes.seek
            if (seek == i) {
                i++
            } else {
                i = seek
            }
            count++
        } else {
            i++
        }
    }

    return count
}

internal inline fun CharSequence.all(startIndex: Int, endIndex: Int, predicate: (Char) -> Boolean): Boolean {
    var i = startIndex
    while (i < endIndex) {
        val c = this[i++]
        if (!predicate(c)) {
            return false
        }
    }

    return true
}

internal inline fun CharSequence.moveSeekUntilDontMatch(startIndex: Int, endIndex: Int, predicate: (Char) -> Boolean): Int {
    var i = startIndex
    while (i < endIndex) {
        val c = this[i]
        if (!predicate(c)) {
            return i
        }
        ++i
    }

    return i
}

internal inline fun CharSequence.moveSeekReverseUntilDontMatch(startIndex: Int, endIndex: Int, predicate: (Char) -> Boolean): Int {
    var i = startIndex
    while (i > endIndex) {
        val c = this[i]
        if (!predicate(c)) {
            return i
        }
        --i
    }

    return i
}

internal fun CharSequence.indexOfChar(char: Char, startIndex: Int, endIndex: Int): Int {
    var i = 0
    while (i < endIndex) {
        if (this[i] == char) {
            return i
        }
        ++i
    }

    return -1
}