package com.kotlinspirit.platform

import java.util.concurrent.ConcurrentHashMap

actual fun <K, V> createConcurrentHashMap(): MutableMap<K, V> {
    return ConcurrentHashMap<K, V>()
}