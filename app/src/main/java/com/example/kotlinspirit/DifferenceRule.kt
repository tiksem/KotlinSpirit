package com.example.kotlinspirit

private class DifferenceRuleIterator<T>(
    private val main: ParseIterator<T>,
    private val exception: ParseIterator<*>,
) : ParseIterator<T> {
    private var previousMainStepCode: Int = -1

    override val seek: Int
        get() = main.seek

    override fun getResult(context: ParseContext): T {
        return main.getResult(context)
    }

    override fun next(context: ParseContext): Int {
        logNext()
        exception.resetSeek(main.seek)
        val mainResult = main.next(context)
        if (!mainResult.hasNext()) {
            return mainResult
        }

        while (true) {
            val code = exception.next(context)
            if (code.isError()) {
                previousMainStepCode = mainResult
                return mainResult
            } else if(code.canComplete()) {
                return if (previousMainStepCode == StepCode.HAS_NEXT_MAY_COMPLETE) {
                    main.prev(context)
                    StepCode.COMPLETE
                } else {
                    StepCode.DIFFERENCE_PREDICATE_FAILED
                }
            }
        }
    }

    override fun resetSeek(seek: Int) {
        main.resetSeek(seek)
        exception.resetSeek(seek)
        previousMainStepCode = -1
    }

    override fun getToken(context: ParseContext): CharSequence {
        return main.getToken(context)
    }

    override fun getBeginSeek(): Int {
        return main.getBeginSeek()
    }

    override fun prev(context: ParseContext) {
        main.prev(context)
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