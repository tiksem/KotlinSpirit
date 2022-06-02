package com.example.kotlinspirit

import java.lang.IllegalStateException

private class RuleBoxIterator<T>(
    private val rule: Rule<T>
): ParseIterator<T> {
    private var iterator: ParseIterator<T> = emptyIterator()

    override fun getResult(context: ParseContext): T {
        return iterator.getResult(context)
    }

    override fun next(context: ParseContext): Int {
        return iterator.next(context)
    }

    override fun prev(context: ParseContext) {
        iterator.prev(context)
    }

    override val seek: Int
        get() = iterator.seek

    override fun getBeginSeek(): Int {
        return iterator.getBeginSeek()
    }

    override fun resetSeek(seek: Int) {
        iterator = rule.iterator
        iterator.resetSeek(seek)
    }
}

class RuleBox<T>: BaseRule<T>() {
    var rule: Rule<T>? = null

    override fun createParseIterator(): ParseIterator<T> {
        return RuleBoxIterator(
            rule ?: throw IllegalStateException("RuleBoxIterator rule hasn't been initialized")
        )
    }
}