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