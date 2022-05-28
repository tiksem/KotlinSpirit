package com.example.kotlinspirit

private class RepeatRuleIterator<T>(
    private val range: IntRange,
    private val iterator: ParseIterator<T>
) : ParseIterator<List<T>> {
    private var beginSeek: Int = -1
    private val results = ArrayList<T>()

    override fun getResult(): List<T> {
        return results
    }

    override fun next(): Int {
        val code = iterator.next()
        if (code.isError()) {
            iterator.resetSeek(iterator.getBeginSeek())
            if (results.size < range.first) {
                return StepCode.REPEAT_NOT_ENOUGH_DATA
            } else {
                return StepCode.COMPLETE
            }
        } else if(code == StepCode.COMPLETE) {
            results.add(iterator.getResult())
            iterator.resetSeek(iterator.seek)
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
        } else {
            return code
        }
    }

    override fun prev() {
        iterator.prev()
    }

    override val seek: Int
        get() = iterator.seek

    override fun getBeginSeek(): Int {
        return beginSeek
    }

    override var sequence: CharSequence
        get() = iterator.sequence
        set(value) {
            iterator.sequence = value
        }

    override fun resetSeek(seek: Int) {
        beginSeek = seek
        iterator.resetSeek(seek)
        results.clear()
    }

    override fun getToken(): CharSequence {
        return sequence.subSequence(beginSeek, seek)
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