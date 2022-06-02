package com.example.kotlinspirit

private class SequenceRuleIterator(
    private val a: Rule<*>,
    private val b: Rule<*>
) : ParseIterator<CharSequence> {
    private var iterator: ParseIterator<*> = EmptyIterator
    private var beginSeek = 0

    override fun next(context: ParseContext): Int {
        logNext()
        val code = iterator.next(context)
        return if (code == StepCode.COMPLETE) {
            if (iterator == a) {
                val seek = skip(context)
                iterator = b.iterator
                iterator.resetSeek(seek)
                iterator.next(context)
            } else {
                val seek = skip(context)
                iterator.resetSeek(seek)
                StepCode.COMPLETE
            }
        } else {
            code
        }
    }

    override fun getResult(context: ParseContext): CharSequence {
        return getToken(context)
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
        iterator = a.iterator
        beginSeek = seek
        iterator.resetSeek(seek)
    }
}

class SequenceRule(
    private val a: Rule<*>,
    private val b: Rule<*>
) : StringRule() {
    override fun createParseIterator(): ParseIterator<CharSequence> {
        return SequenceRuleIterator(a, b)
    }
}