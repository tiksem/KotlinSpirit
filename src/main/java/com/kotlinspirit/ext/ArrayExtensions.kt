package com.kotlinspirit.ext

import java.util.*

internal fun IntArray.setRegion(startIndex: Int, array: IntArray) {
    for (i in startIndex until array.size) {
        this[i] = array[i - startIndex]
    }
}

internal fun CharArray.binarySearchContains(char: Char): Boolean {
    return Arrays.binarySearch(this, char) >= 0
}

internal fun <T> Array<T>.contains(predicate: (T) -> Boolean): Boolean {
    return find(predicate) != null
}

internal fun CharArray.removeAllContainedInGivenSortedArrayOrGivenRanges(
    sortedArray: CharArray,
    ranges: List<CharRange>
): CharArray {
    val result = CharArray(size)
    var i = 0
    forEach {
        if (
            !sortedArray.binarySearchContains(it) &&
            !ranges.any { range ->
                range.contains(it)
            }
        ) {
            result[i++] = it
        }
    }

    return result.copyOf(newSize = i)
}