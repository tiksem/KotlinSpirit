package com.example.kotlinspirit

import java.util.*

operator fun <T> SortedSet<T>.plus(elements: SortedSet<T>): SortedSet<T> {
    return TreeSet(this).also {
        it.addAll(elements)
    }
}

operator fun <T> SortedSet<T>.minus(elements: SortedSet<T>): SortedSet<T> {
    return TreeSet(this).also {
        it.removeAll(elements)
    }
}