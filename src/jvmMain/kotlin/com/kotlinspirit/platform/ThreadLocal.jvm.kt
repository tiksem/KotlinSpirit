package com.kotlinspirit.platform

actual fun <T> createThreadLocal(initializer: () -> T): ThreadLocal<T> {
    return object : ThreadLocal<T> {
        private val threadLocal = java.lang.ThreadLocal.withInitial { initializer() }

        override fun get(): T {
            return threadLocal.get()
        }
    }
}