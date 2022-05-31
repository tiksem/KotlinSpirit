package com.example.kotlinspirit

private class RepeatRuleIterator<T>(
    private val range: IntRange,
    private val iterator: ParseIterator<T>
) : ParseIterator<List<T>> {
    private var beginSeek: Int = -1
    private val results = ArrayList<T>()

    override fun getResult(context: ParseContext): List<T> {
        return results
    }

    override fun next(context: ParseContext): Int {
        val code = iterator.next(context)
        when {
            code.isError() -> {
                iterator.resetSeek(iterator.getBeginSeek())
                return if (results.size < range.first) {
                    StepCode.REPEAT_NOT_ENOUGH_DATA
                } else {
                    StepCode.COMPLETE
                }
            }
            code == StepCode.COMPLETE -> {
                results.add(iterator.getResult(context))
                val seek = skip(context)
                iterator.resetSeek(seek)

                return when {
                    results.size < range.first -> {
                        StepCode.HAS_NEXT
                    }
                    results.size == range.last -> {
                        StepCode.COMPLETE
                    }
                    else -> {
                        StepCode.HAS_NEXT_MAY_COMPLETE
                    }
                }
            }
            else -> {
                return code
            }
        }
    }

    override fun prev(context: ParseContext) {
        iterator.prev(context)
    }

    override val seek: Int
        get() = iterator.seek

    override fun getBeginSeek(): Int {
        return beginSeek
    }

    override fun resetSeek(seek: Int) {
        beginSeek = seek
        iterator.resetSeek(seek)
        results.clear()
    }
}

class RepeatRule<T>(
    private val range: IntRange,
    private val rule: Rule<T>
) : BaseRule<List<T>>() {
    override fun createParseIterator(): ParseIterator<List<T>> {
        return RepeatRuleIterator(range, rule.iterator)
    }
}