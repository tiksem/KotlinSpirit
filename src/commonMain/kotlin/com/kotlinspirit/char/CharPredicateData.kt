package com.kotlinspirit.char

import com.kotlinspirit.ext.*
import com.kotlinspirit.ext.excludeRanges
import com.kotlinspirit.ext.includeRanges
import java.util.*

internal class CharPredicateData(
    chars: CharArray,
    ranges: List<CharRange>
) {
    val chars: CharArray
    val ranges: List<CharRange>

    init {
        this.ranges = ranges.filter {
            it.first != it.last
        }

        if (this.ranges.size != ranges.size) {
            this.chars = chars.toMutableList().also {
                it.addAll(ranges.map { range ->
                    range.first
                })
            }.toCharArray()
        } else {
            this.chars = chars
        }
    }

    constructor(chars: CharArray) : this(chars, emptyList()) {
    }

    constructor(ranges: Array<out CharRange>) : this(charArrayOf(), ranges.toList()) {
    }

    operator fun plus(other: CharPredicateData): CharPredicateData {
        return CharPredicateData(
            chars = charArrayOf(*chars, *other.chars).distinctSortedArray(),
            ranges = ranges.includeRanges(other.ranges)
        )
    }

    operator fun minus(other: CharPredicateData): CharPredicateData {
        val rangesToExclude = if (other.chars.isEmpty()) {
            other.ranges
        } else {
            ArrayList(other.ranges).also { arr ->
                arr.addAll(other.chars.map {
                    it..it
                })
            }
        }
        val chars = this.chars.filter { ch ->
            !other.ranges.any {
                it.contains(ch)
            } && !other.chars.binarySearchContains(ch)
        }
        return CharPredicateData(
            chars = chars,
            ranges = ranges.excludeRanges(rangesToExclude)
        )
    }

    fun toPredicate(): (Char) -> Boolean {
        return CharPredicates.from(
            ranges = ranges.toTypedArray(),
            chars = chars
        )
    }

    fun isExactChar(): Boolean {
        return chars.size == 1 && ranges.isEmpty()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        val o = other as? CharPredicateData ?: return false

        return o.chars.contentEquals(chars) && o.ranges == ranges
    }

    override fun hashCode(): Int {
        var result = chars.hashCode()
        result = 31 * result + ranges.hashCode()
        return result
    }
}