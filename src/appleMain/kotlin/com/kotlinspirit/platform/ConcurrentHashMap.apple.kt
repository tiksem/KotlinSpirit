package com.kotlinspirit.platform

import co.touchlab.stately.collections.ConcurrentMutableMap

actual fun <K, V> createConcurrentHashMap(): MutableMap<K, V> {
    return ConcurrentMutableMap()
}