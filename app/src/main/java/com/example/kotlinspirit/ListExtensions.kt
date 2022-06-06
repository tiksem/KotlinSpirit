package com.example.kotlinspirit

fun <T> List<T>.asArrayList(): ArrayList<T> {
    if (this is ArrayList) {
        return this
    } else {
        return ArrayList(this)
    }
}