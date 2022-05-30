package com.example.kotlinspirit

interface ResultProvider<T> {
    fun getResult(): T
}

interface ParseIterator<T> : ResultProvider<T> {
    fun next(): Int
    fun prev()
    val seek: Int
    var sequence: CharSequence
    var skipper: ParseIterator<*>?
        get() = null
        set(value) {
        }
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

    fun getTokenLength(): Int {
        return seek - getBeginSeek()
    }

    fun skip(seek: Int) {
        resetSeek(seek)
        while (true) {
            val next = next()
            if (next == StepCode.COMPLETE) {
                return
            } else if (next.isError()) {
                resetSeek(seek)
                return
            }
        }
    }
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