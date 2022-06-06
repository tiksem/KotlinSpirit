package com.example.kotlinspirit

import java.lang.IllegalStateException

data class Box<T : Any>(
    var data: T? = null
) {
    fun requireData(): T {
        return data ?: throw IllegalStateException("data was not initialised")
    }
}