package com.example.kotlinspirit

import java.lang.IllegalStateException

private fun Array<out CharRange>.contains(char: Char): Boolean {
    return contains { range ->
        range.contains(char)
    }
}

object CharPredicates {
    fun from(vararg chars: Char): (Char) -> Boolean {
        return if (chars.size == 1) {
            val char = chars[0]
            {
                char == it
            }
        } else {
            {
                chars.binarySearchContains(it)
            }
        }
    }

    fun from(vararg ranges: CharRange): (Char) -> Boolean {
        return if (ranges.size == 1) {
            val range = ranges[0]
            {
                range.contains(it)
            }
        } else {
            {
                ranges.contains(it)
            }
        }
    }

    fun from(
        ranges: Array<CharRange>,
        chars: CharArray
    ): (Char) -> Boolean  {
        assert(ranges.isNotEmpty() || chars.isNotEmpty())
        return when {
            ranges.isEmpty() -> from(*chars)
            chars.isEmpty() -> from(*ranges)
            ranges.size == 1 && chars.size == 1 -> {
                val ch = chars[0]
                val range = ranges[0]
                {
                    ch == it || range.contains(it)
                }
            }
            ranges.size > 1 && chars.size > 1 -> {
                val sorted = chars.sortedArray()
                return { char: Char ->
                    sorted.binarySearchContains(char) || ranges.contains(char)
                }
            }
            ranges.size > 1 && chars.size == 1 -> {
                val ch = chars[0]
                {
                    ch == it || ranges.contains(it)
                }
            }
            ranges.size == 1 && chars.size > 1 -> {
                val sorted = chars.sortedArray()
                val range = ranges[0]
                {
                    range.contains(it) || sorted.binarySearchContains(it)
                }
            }
            else -> throw IllegalStateException("Unexpected behaviour")
        }
    }
}