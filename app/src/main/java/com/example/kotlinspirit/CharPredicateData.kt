package com.example.kotlinspirit

import com.example.kotlinspirit.ext.excludeRanges
import com.example.kotlinspirit.ext.includeRanges
import com.example.kotlinspirit.ext.minus
import com.example.kotlinspirit.ext.plus
import java.util.*

internal class CharPredicateData(
    chars: SortedSet<Char>,
    ranges: List<CharRange>
) {
    val chars: SortedSet<Char>
    val ranges: List<CharRange>

    init {
        this.ranges = ranges.filter {
            it.first != it.last
        }

        if (this.ranges.size != ranges.size) {
            this.chars = TreeSet(chars).also {
                it.addAll(ranges.map { range ->
                    range.first
                })
            }
        } else {
            this.chars = chars
        }
    }

    constructor(chars: CharArray) : this(chars.toSortedSet(), emptyList()) {
    }

    constructor(ranges: Array<out CharRange>) : this(sortedSetOf(), ranges.toList()) {
    }

    operator fun plus(other: CharPredicateData): CharPredicateData {
        return CharPredicateData(
            chars = other.chars + chars,
            ranges = ranges.includeRanges(other.ranges)
        )
    }

    operator fun minus(other: CharPredicateData): CharPredicateData {
        return CharPredicateData(
            chars = chars - other.chars,
            ranges = ranges.excludeRanges(other.ranges)
        )
    }

    fun toPredicate(): (Char) -> Boolean {
        return CharPredicates.from(
            ranges = ranges.toTypedArray(),
            chars = chars.toCharArray()
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharPredicateData

        if (chars != other.chars) return false
        if (ranges != other.ranges) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chars.hashCode()
        result = 31 * result + ranges.hashCode()
        return result
    }
}