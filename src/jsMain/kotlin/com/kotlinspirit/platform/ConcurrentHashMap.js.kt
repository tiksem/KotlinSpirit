package com.kotlinspirit.platform

actual fun <K, V> createConcurrentHashMap(): MutableMap<K, V> {
    return HashMap()
}