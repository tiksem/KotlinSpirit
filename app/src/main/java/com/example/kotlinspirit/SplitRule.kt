package com.example.kotlinspirit

import java.lang.IllegalStateException

private class SplitRuleIterator<T>(
    private val range: IntRange,
    private val token: ParseIterator<T>,
    private val divider: ParseIterator<*>
) : ParseIterator<List<T>> {
    private val results = ArrayList<T>()
    private var lastTokenSeek = -1

    override fun getResult(): List<T> {
        return results
    }

    private fun addResult() {
        results.add(token.getResult())
        lastTokenSeek = token.seek
    }

    private fun getCompleteCode(): Int {
        return if (results.size >= range.first) {
            StepCode.COMPLETE
        } else {
            StepCode.SPLIT_NOT_ENOUGH_DATA
        }
    }

    override fun next(): Int {
        val tokenSeekBefore = token.seek
        val code = token.next()
        when {
            code == StepCode.HAS_NEXT -> return code
            code == StepCode.HAS_NEXT_MAY_COMPLETE -> {
                divider.resetSeek(tokenSeekBefore)
                while (true) {
                    val dividerCode = divider.next()
                    if (dividerCode == StepCode.COMPLETE) {
                        addResult()
                        return when {
                            results.size < range.first -> {
                                token.resetSeek(divider.seek)
                                StepCode.HAS_NEXT
                            }
                            results.size == range.last -> {
                                StepCode.COMPLETE
                            }
                            else -> {
                                token.resetSeek(divider.seek)
                                StepCode.HAS_NEXT_MAY_COMPLETE
                            }
                        }
                    } else if (dividerCode.isError()) {
                        return code
                    }
                }
            }
            code.isError() -> {
                token.resetSeek(lastTokenSeek)
                return getCompleteCode()
            }
            code == StepCode.COMPLETE -> {
                addResult()
                if (results.size == range.last) {
                    return StepCode.COMPLETE
                }
                divider.resetSeek(token.seek)
                while (true) {
                    val dividerCode = divider.next()
                    if (dividerCode == StepCode.COMPLETE) {
                        token.resetSeek(divider.seek)
                        return when {
                            results.size < range.first -> {
                                StepCode.HAS_NEXT
                            }
                            else -> {
                                StepCode.HAS_NEXT_MAY_COMPLETE
                            }
                        }
                    } else if (dividerCode.isError()) {
                        return getCompleteCode()
                    }
                }
            }
            else -> throw IllegalStateException("Unreachable code")
        }
    }

    override fun prev() {
        token.prev()
        if (lastTokenSeek == token.seek || divider.seek == token.seek) {
            results.removeLastOrNull()
        }
    }

    override fun resetSeek(seek: Int) {
        token.resetSeek(seek)
        divider.resetSeek(seek)
        results.clear()
        lastTokenSeek = seek
    }

    override val seek: Int
        get() = token.seek

    override fun getBeginSeek(): Int {
        return token.getBeginSeek()
    }

    override var sequence: CharSequence
        get() = token.sequence
        set(value) {
            token.sequence = value
            divider.sequence = value
        }

    override fun getToken(): CharSequence {
        return token.getToken()
    }
}

class SplitRule<T>(
    private val range: IntRange,
    private val tokenRule: Rule<T>,
    private val dividerRule: Rule<*>
) : BaseRule<List<T>>() {
    override fun createParseIterator(): ParseIterator<List<T>> {
        return SplitRuleIterator(
            range = range,
            token = tokenRule.iterator,
            divider = dividerRule.iterator
        )
    }
}