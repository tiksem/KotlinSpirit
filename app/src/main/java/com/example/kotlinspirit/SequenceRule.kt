package com.example.kotlinspirit

private class SequenceRuleIterator(
    private val a: ParseIterator<*>,
    private val b: ParseIterator<*>
) : ParseIterator<CharSequence> {
    private var sequence: CharSequence = ""
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
                b.next()
            } else {
                StepCode.COMPLETE
            }
        } else {
            code
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

    override fun setSequence(string: CharSequence, length: Int) {
        a.setSequence(string, length)
        b.setSequence(string, length)
        this.sequence = string
    }

    override fun resetSeek(seek: Int) {
        a.resetSeek(seek)
        iterator = a
    }

    override fun getToken(): CharSequence {
        return sequence.subSequence(a.getBeginSeek(), b.seek)
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