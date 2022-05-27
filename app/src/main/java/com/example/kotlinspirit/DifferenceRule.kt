package com.example.kotlinspirit

private class DifferenceRuleIterator<T>(
    private val main: ParseIterator<T>,
    private val exception: ParseIterator<*>,
) : ParseIterator<T> {
    private var previousMainStepCode: Int = -1

    override val seek: Int
        get() = main.seek

    override fun getResult(): T {
        return main.getResult()
    }

    override fun next(): Int {
        exception.resetSeek(main.seek)
        val mainResult = main.next()
        if (!mainResult.hasNext()) {
            return mainResult
        }

        while (true) {
            val code = exception.next()
            if (code.isError()) {
                previousMainStepCode = mainResult
                return mainResult
            } else if(code.canComplete()) {
                return if (previousMainStepCode == StepCode.HAS_NEXT_MAY_COMPLETE) {
                    main.prev()
                    StepCode.COMPLETE
                } else {
                    StepCode.DIFFERENCE_PREDICATE_FAILED
                }
            }
        }
    }

    override fun setSequence(string: CharSequence, length: Int) {
        main.setSequence(string, length)
        exception.setSequence(string, length)
    }

    override fun resetSeek(seek: Int) {
        main.resetSeek(seek)
        exception.resetSeek(seek)
        previousMainStepCode = -1
    }

    override fun getToken(): CharSequence {
        return main.getToken()
    }

    override fun getBeginSeek(): Int {
        return main.getBeginSeek()
    }

    override fun prev() {
        main.prev()
    }
}

class DifferenceRule<T>(
    private val main: Rule<T>,
    private val exception: Rule<*>
) : BaseRule<T>() {
    override fun createParseIterator(): ParseIterator<T> {
        return DifferenceRuleIterator(main.iterator, exception.iterator)
    }
}