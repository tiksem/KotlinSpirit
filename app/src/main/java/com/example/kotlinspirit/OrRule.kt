package com.example.kotlinspirit

private class OrRuleIterator<T>(
    private val aIterator: ParseIterator<T>,
    private val bIterator: ParseIterator<T>,
) : ParseIterator<T> {
    private var iterator = aIterator
    private var previousStepCode: Int = -1

    override val seek
        get() = iterator.seek

    override fun getResult(): T {
        return iterator.getResult()
    }

    override fun next(): Int {
        val code = iterator.next()
        val result = if (code.isError()) {
            when {
                previousStepCode == StepCode.HAS_NEXT_MAY_COMPLETE -> {
                    prev()
                    StepCode.COMPLETE
                }
                iterator == bIterator -> {
                    code
                }
                else -> {
                    iterator = bIterator
                    previousStepCode = -1
                    return StepCode.HAS_NEXT
                }
            }
        } else {
            code
        }
        previousStepCode = result
        return result
    }

    override fun setSequence(string: CharSequence, length: Int) {
        aIterator.setSequence(string, length)
        bIterator.setSequence(string, length)
        iterator = aIterator
    }

    override fun resetSeek(seek: Int) {
        aIterator.resetSeek(seek)
        bIterator.resetSeek(seek)
        previousStepCode = -1
    }

    override fun prev() {
        iterator.prev()
    }

    override fun getBeginSeek(): Int {
        return iterator.getBeginSeek()
    }

    override fun getToken(): CharSequence {
        return iterator.getToken()
    }
}

class OrRule<T>(
    private val a: Rule<T>,
    private val b: Rule<T>
) : BaseRule<T>() {
    override fun createParseIterator(): ParseIterator<T> {
        return OrRuleIterator(a.iterator, b.iterator)
    }
}