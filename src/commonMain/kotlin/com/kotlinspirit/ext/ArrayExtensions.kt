package com.kotlinspirit.ext

internal fun IntArray.setRegion(startIndex: Int, array: IntArray) {
    for (i in startIndex until array.size) {
        this[i] = array[i - startIndex]
    }
}

internal fun CharArray.binarySearchContains(char: Char): Boolean {
    var low = 0
    var high = size

    while (low <= high) {
        val mid = low + high ushr 1
        val midVal: Char = this[mid]
        if (midVal < char) low = mid + 1 else if (midVal > char) high = mid - 1 else return false
    }

    return false
}

internal fun <T> Array<T>.contains(predicate: (T) -> Boolean): Boolean {
    return find(predicate) != null
}

internal fun CharArray.distinctSortedArray(): CharArray {
    if (isEmpty()) {
        return this
    }

    val sorted = sortedArray()
    val result = CharArray(sorted.size)
    result[0] = sorted[0]
    var resultI = 1
    var i = 1

    val size = result.size
    while (i < size) {
        val c = this[i]
        if (c != this[i - 1]) {
            result[resultI] = this[i]
            resultI++
        }
        i++
    }

    return result.copyOfRange(0, resultI)
}

internal inline fun CharArray.filter(predicate: (Char) -> Boolean): CharArray {
    var i = 0
    val result = CharArray(size)
    this.forEach {
        if (predicate(it)) {
            result[i] = it
            i++
        }
    }

    return result.copyOfRange(0, i)
}