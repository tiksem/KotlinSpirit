package com.example.kotlinspirit

interface ResultProvider<T> {
    fun getResult(context: ParseContext): T
}

interface ParseIterator<T> : ResultProvider<T> {
    fun next(context: ParseContext): Int
    fun prev(context: ParseContext)
    val seek: Int
    fun getBeginSeek(): Int
    fun resetSeek(seek: Int)

    fun isEof(context: ParseContext): Boolean {
        return seek >= context.string.length
    }

    fun <To> transform(func: (T) -> To): ParseIterator<To> {
        return object : ParseIterator<To> by this as ParseIterator<To> {
            override fun getResult(context: ParseContext): To {
                return func(this@ParseIterator.getResult(context))
            }
        }
    }

    fun getToken(context: ParseContext): CharSequence {
        return context.string.subSequence(getBeginSeek(), seek)
    }

    fun getTokenLength(): Int {
        return seek - getBeginSeek()
    }

    fun skip(context: ParseContext): Int {
        val skipper = context.skipper?.iterator ?: return seek
        skipper.resetSeek(seek)
        while (true) {
            val next = skipper.next(context)
            if (next == StepCode.COMPLETE) {
                return skipper.seek
            } else if (next.isError()) {
                return seek
            }
        }
    }
}

abstract class BaseParseIterator<T>: ParseIterator<T> {
    protected var seekBegin: Int = 0
    override var seek = seekBegin

    override fun resetSeek(seek: Int) {
        this.seekBegin = seek
        this.seek = seek
    }

    override fun getBeginSeek(): Int {
        return seekBegin
    }

    protected fun ParseContext.readChar(): Char {
        return string[seek++]
    }

    protected fun ParseContext.getChar(): Char {
        return string[seek]
    }

    override fun prev(context: ParseContext) {
        seek--
    }
}

abstract class BaseStringIterator : BaseParseIterator<CharSequence>() {
    override fun getResult(context: ParseContext): CharSequence {
        return getToken(context)
    }
}