package com.example.kotlinspirit

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class CharMatchData(
    ranges: Array<CharRange>,
    chars: CharArray
) {
    private var ranges = ranges.distinct().toTypedArray()
    private var chars = chars.distinct().sorted().toCharArray()

    fun merge(other: CharMatchData): CharMatchData {
        return CharMatchData(
            ranges = ranges + other.ranges,
            chars = chars + other.chars
        )
    }

    fun isAnyChar(): Boolean {
        return ranges.isEmpty() && chars.isEmpty()
    }

    private fun createPredicate(): (Char) -> Boolean {
        when {
            ranges.size == 1 && chars.isEmpty() -> {
                val range = ranges[0]
                return {
                    range.contains(it)
                }
            }

            ranges.isEmpty() && chars.size == 1 -> {
                val char = chars[0]
                return {
                    char == it
                }
            }

            ranges.size == 1 && chars.size == 1 -> {
                val char = chars[0]
                val range = ranges[0]

                return {
                    char == it || it in range
                }
            }

            ranges.isEmpty() && chars.isNotEmpty() -> {
                return {
                    chars.binarySearchContains(it)
                }
            }

            ranges.isNotEmpty() && chars.isEmpty() -> {
                return { ch ->
                    ranges.find { range ->
                        ch in range
                    } != null
                }
            }

            ranges.size == 1 && chars.isNotEmpty() -> {
                val range = ranges[0]
                return {
                    range.contains(it) || chars.binarySearchContains(it)
                }
            }

            ranges.isNotEmpty() && chars.size == 1 -> {
                val char = chars[0]
                return { ch ->
                    ch == char || ranges.find { range ->
                        ch in range
                    } != null
                }
            }

            else -> {
                return { ch ->
                    ranges.find { range ->
                        ch in range
                    } != null || chars.binarySearchContains(ch)
                }
            }
        }
    }

    val predicateIndex: Int
        get() {
            return predicateHash[this] ?: createPredicate().let {
                predicates.add(it)
                val index = predicates.indexOf(it)
                predicateHash[this] = index
                index
            }
        }

    val predicate: (Char) -> Boolean
        get() = predicates[predicateIndex]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharMatchData

        if (ranges != other.ranges) return false
        if (!chars.contentEquals(other.chars)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ranges.hashCode()
        result = 31 * result + chars.contentHashCode()
        return result
    }

    companion object {
        private val predicateHash = ConcurrentHashMap<CharMatchData, Int>()
        val predicates = CopyOnWriteArrayList<(Char) -> Boolean>()

        fun any(): CharMatchData {
            return CharMatchData(ranges = emptyArray(), chars = charArrayOf())
        }
    }
}