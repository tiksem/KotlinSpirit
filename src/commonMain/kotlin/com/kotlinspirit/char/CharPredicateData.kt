package com.kotlinspirit.char

import com.kotlinspirit.ext.*
import com.kotlinspirit.ext.excludeRanges
import com.kotlinspirit.ext.includeRanges

internal class CharPredicateData(
    chars: CharArray, // should be sorted
    ranges: List<CharRange>
) {
    val chars: CharArray
    val ranges: List<CharRange>

    init {
        this.ranges = ranges.filter {
            it.first != it.last
        }

        if (this.ranges.size != ranges.size) {
            val singleChars = ranges.filter {
                it.first == it.last
            }.map {
                it.first
            }
            this.chars = buildList {
                addAll(chars.toList())
                addAll(singleChars)
            }.sorted().distinct().toCharArray()
        } else {
            this.chars = chars.sortedArray()
        }
    }

    constructor(chars: CharArray) : this(chars, emptyList())

    constructor(ranges: Array<out CharRange>) : this(charArrayOf(), ranges.toList())

    operator fun plus(other: CharPredicateData): CharPredicateData {
        return CharPredicateData(
            chars = buildList {
                addAll(other.chars.toList())
                addAll(chars.toList())
            }.toCharArray(),
            ranges = ranges.includeRanges(other.ranges)
        )
    }

    operator fun minus(other: CharPredicateData): CharPredicateData {
        val rangesToExclude = if (other.chars.isEmpty()) {
            other.ranges
        } else {
            buildList {
                addAll(other.ranges)
                addAll(other.chars.map { it..it })
            }
        }
        return CharPredicateData(
            chars = chars.removeAllContainedInGivenSortedArrayOrGivenRanges(
                sortedArray = other.chars,
                ranges = other.ranges
            ),
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
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CharPredicateData

        if (!chars.contentEquals(other.chars)) return false
        if (ranges != other.ranges) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chars.contentHashCode()
        result = 31 * result + ranges.hashCode()
        return result
    }
}