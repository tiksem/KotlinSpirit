package com.kotlinspirit.platform

import kotlinx.cinterop.*
import platform.posix.*

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun <T> createThreadLocal(initializer: () -> T): ThreadLocal<T> {
    return object : ThreadLocal<T> {
        private val key: pthread_key_t

        init {
            val keyPtr = nativeHeap.alloc<pthread_key_tVar>()
            pthread_key_create(
                keyPtr.ptr,
                staticCFunction { ptr ->
                    ptr?.asStableRef<Any>()?.dispose()
                }
            )
            key = keyPtr.value
        }


        override fun get(): T {
            val stored = pthread_getspecific(key)

            if (stored == null) {
                val value = initializer()
                val stableRef = StableRef.create(value as Any)
                pthread_setspecific(key, stableRef.asCPointer())
                return value
            }

            @Suppress("UNCHECKED_CAST")
            return stored.asStableRef<Any>().get() as T
        }
    }
}