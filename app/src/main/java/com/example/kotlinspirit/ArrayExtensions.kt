package com.example.kotlinspirit

import java.util.*

fun IntArray.setRegion(startIndex: Int, array: IntArray) {
    for (i in startIndex until array.size) {
        this[i] = array[i - startIndex]
    }
}

fun CharArray.binarySearchContains(char: Char): Boolean {
    return Arrays.binarySearch(this, char) >= 0
}