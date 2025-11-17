package com.kotlinspirit.platform

actual fun <T> createThreadLocal(initializer: () -> T): ThreadLocal<T> {
    return object : ThreadLocal<T> {
        override fun get(): T {
            return initializer()
        }
    }
}