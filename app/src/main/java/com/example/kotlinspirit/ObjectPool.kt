package com.example.kotlinspirit

class ObjectPool<T>(
    private val factory: () -> T
) {
    private val objects = ArrayList<T>(10)

    fun take(): T {
        return objects.removeLastOrNull() ?: factory()
    }

    fun putBack(o: T) {
        objects.add(o)
    }
}