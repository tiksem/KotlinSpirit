package com.example.kotlinspirit

private class SequenceRuleIterator(
    private val a: ParseIterator<*>,
    private val b: ParseIterator<*>
) : ParseIterator<CharSequence> {
    private var iterator = a

    override fun getResult(): CharSequence {
        return getToken()
    }

    override fun next(): Int {
        val code = iterator.next()
        return if (code == StepCode.COMPLETE) {
            if (iterator == a) {
                iterator = b
                b.resetSeek(a.seek)
                skipIfNeed()
                b.next()
            } else {
                skipIfNeed()
                StepCode.COMPLETE
            }
        } else {
            code
        }
    }

    private fun skipIfNeed() {
        val skipper = this.skipper
        if (skipper != null) {
            skipper.skip(iterator.seek)
            iterator.resetSeek(skipper.seek)
        }
    }

    override fun prev() {
        TODO("Not yet implemented")
    }

    override val seek: Int
        get() = iterator.seek

    override fun getBeginSeek(): Int {
        return a.getBeginSeek()
    }

    override var sequence: CharSequence
        get() = a.sequence
        set(value) {
            a.sequence = value
            b.sequence = value
        }

    override fun resetSeek(seek: Int) {
        a.resetSeek(seek)
        iterator = a
    }

    override fun getToken(): CharSequence {
        return sequence.subSequence(a.getBeginSeek(), b.seek)
    }

    override var skipper: ParseIterator<*>?
        get() = a.skipper
        set(value) {
            a.skipper = value
            b.skipper = value
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