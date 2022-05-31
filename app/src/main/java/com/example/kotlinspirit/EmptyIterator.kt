package com.example.kotlinspirit

import java.lang.IllegalStateException

object EmptyIterator : BaseParseIterator<Any>() {
    override fun getResult(context: ParseContext): Any {
        throw IllegalStateException("No result")
    }

    override fun next(context: ParseContext): Int {
        return StepCode.EMPTY_ITERATOR_ERROR
    }
}

fun <T> emptyIterator(): ParseIterator<T> {
    return EmptyIterator as ParseIterator<T>
}