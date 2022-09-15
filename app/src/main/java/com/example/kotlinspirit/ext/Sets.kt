package com.example.kotlinspirit.ext

import java.util.*

internal operator fun <T> SortedSet<T>.plus(elements: SortedSet<T>): SortedSet<T> {
    return TreeSet(this).also {
        it.addAll(elements)
    }
}

internal operator fun <T> SortedSet<T>.minus(elements: SortedSet<T>): SortedSet<T> {
    return TreeSet(this).also {
        it.removeAll(elements)
    }
}