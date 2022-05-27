package com.example.kotlinspirit

import java.util.*

fun <T> Array<T>.contains(predicate: (T) -> Boolean): Boolean {
    return find(predicate) != null
}

fun CharArray.binarySearchContains(char: Char): Boolean {
    return Arrays.binarySearch(this, char) >= 0
}