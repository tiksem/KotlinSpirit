package com.example.kotlinspirit

interface ResultProvider<T> {
    fun getResult(): T
}

interface ParseIterator<T> : ResultProvider<T> {
    fun next(): Int
    fun prev()
    val seek: Int
    fun getBeginSeek(): Int
    fun setSequence(
        string: CharSequence,
        length: Int
    )
    fun resetSeek(seek: Int)

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
    protected var string: CharSequence = ""
    protected var length: Int = 0
    protected var seekBegin: Int = 0
    override var seek = seekBegin

    override fun setSequence(
        string: CharSequence,
        length: Int
    ) {
        this.string = string
        this.length = length
    }

    override fun resetSeek(seek: Int) {
        this.seekBegin = seek
        this.seek = seek
    }

    override fun getBeginSeek(): Int {
        return seekBegin
    }

    protected fun readChar(): Char {
        return string[seek++]
    }

    protected fun getChar(): Char {
        return string[seek]
    }

    protected fun isEof(): Boolean {
        return seek >= length
    }

    override fun getToken(): CharSequence {
        return string.subSequence(seekBegin, seek)
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