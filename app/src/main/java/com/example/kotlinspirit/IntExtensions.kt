package com.example.kotlinspirit

fun Int.positiveOrZero(): Int{
    return if (this < 0) {
        return 0
    } else {
        return this
    }
}