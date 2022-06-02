package com.example.kotlinspirit

private class OrRuleIterator<T>(
    private val a: Rule<T>,
    private val b: Rule<T>,
) : ParseIterator<T> {
    private var iterator: ParseIterator<T> = emptyIterator()
    private var previousStepCode: Int = -1
    private var aFailed = false

    override val seek get() = iterator.seek

    override fun getResult(context: ParseContext): T {
        return iterator.getResult(context)
    }

    override fun next(context: ParseContext): Int {
        logNext()
        if (iterator == EmptyIterator) {
            val seek = this.seek
            iterator = a.iterator
            iterator.resetSeek(seek)
        }

        val code = iterator.next(context)
        val result = if (code.isError()) {
            when {
                previousStepCode == StepCode.HAS_NEXT_MAY_COMPLETE -> {
                    prev(context)
                    StepCode.COMPLETE
                }
                aFailed -> {
                    code
                }
                else -> {
                    aFailed = true
                    val seek = getBeginSeek()
                    iterator = b.iterator
                    iterator.resetSeek(seek)
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

    override fun resetSeek(seek: Int) {
        aFailed = false
        iterator = emptyIterator()
        iterator.resetSeek(seek)
        previousStepCode = -1
    }

    override fun prev(context: ParseContext) {
        iterator.prev(context)
    }

    override fun getBeginSeek(): Int {
        return iterator.getBeginSeek()
    }

    override fun getToken(context: ParseContext): CharSequence {
        return iterator.getToken(context)
    }
}

class OrRule<T>(
    private val a: Rule<T>,
    private val b: Rule<T>
) : BaseRule<T>() {
    override fun createParseIterator(): ParseIterator<T> {
        return OrRuleIterator(a, b)
    }
}