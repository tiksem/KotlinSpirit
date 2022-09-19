package com.example.kotlinspirit.ext

import java.util.*
import java.util.function.Predicate

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

internal fun <T> MutableSet<T>.eraseIf(predicate: (T) -> Boolean) {
    val each = iterator()
    while (each.hasNext()) {
        if (predicate(each.next())) {
            each.remove()
        }
    }
}