package com.example.kotlinspirit

private class SequenceRuleIterator(
    private val a: ParseIterator<*>,
    private val b: ParseIterator<*>
) : ParseIterator<CharSequence> {
    private var iterator = a

    override fun next(context: ParseContext): Int {
        val code = iterator.next(context)
        return if (code == StepCode.COMPLETE) {
            if (iterator == a) {
                val seek = skip(context)
                iterator = b
                b.resetSeek(seek)
                b.next(context)
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
        return a.getBeginSeek()
    }

    override fun resetSeek(seek: Int) {
        a.resetSeek(seek)
        iterator = a
    }
}

class SequenceRule(
    private val a: Rule<*>,
    private val b: Rule<*>
) : StringRule() {
    override fun createParseIterator(): ParseIterator<CharSequence> {
        return SequenceRuleIterator(a.iterator, b.iterator)
    }
}