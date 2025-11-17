package com.kotlinspirit.ext

internal fun IntArray.setRegion(startIndex: Int, array: IntArray) {
    for (i in startIndex until array.size) {
        this[i] = array[i - startIndex]
    }
}

internal fun CharArray.binarySearchContains(char: Char): Boolean {
    var low = 0
    var high = size - 1

    while (low <= high) {
        val mid = (low + high) ushr 1
        val midVal = this[mid]

        when {
            midVal < char -> low = mid + 1
            midVal > char -> high = mid - 1
            else -> return true
        }
    }
    return false
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