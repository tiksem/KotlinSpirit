package com.example.kotlinspirit

interface ResultProvider<T> {
    fun getResult(): T
}

interface ParseIterator<T> : ResultProvider<T> {
    fun next(): Int
    fun prev()
    val seek: Int
    var sequence: CharSequence
    fun getBeginSeek(): Int
    fun resetSeek(seek: Int)
    fun isEof(): Boolean {
        return seek >= sequence.length
    }

    fun <To> transform(func: (T) -> To): ParseIterator<To> {
        return object : ParseIterator<To> by this as ParseIterator<To> {
            override fun getResult(): To {
                return func(this@ParseIterator.getResult())
            }
        }
    }

    fun getToken(): CharSequence
}

abstract class BaseParseIterator<T>: ParseIterator<T> {
    override var sequence: CharSequence = ""
    protected var seekBegin: Int = 0
    override var seek = seekBegin

    override fun resetSeek(seek: Int) {
        this.seekBegin = seek
        this.seek = seek
    }

    override fun getBeginSeek(): Int {
        return seekBegin
    }

    protected fun readChar(): Char {
        return sequence[seek++]
    }

    protected fun getChar(): Char {
        return sequence[seek]
    }

    override fun getToken(): CharSequence {
        return sequence.subSequence(seekBegin, seek)
    }

    override fun prev() {
        seek--
    }
}

abstract class BaseStringIterator : BaseParseIterator<CharSequence>() {
    override fun getResult(): CharSequence {
        return getToken()
    }
}