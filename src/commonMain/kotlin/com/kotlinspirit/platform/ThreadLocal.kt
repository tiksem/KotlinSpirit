package com.kotlinspirit.platform

expect fun <T> createThreadLocal(initializer: () -> T): ThreadLocal<T>

interface ThreadLocal<T> {
    fun get(): T
}