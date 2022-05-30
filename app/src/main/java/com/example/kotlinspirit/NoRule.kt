package com.example.kotlinspirit

private class NoIterator<T>(
    private val iterator: ParseIterator<T>
) : ParseIterator<CharSequence> {
    private var beginSeek = iterator.seek
    private var errorSeek: Int = beginSeek

    override var sequence: CharSequence
        get() = iterator.sequence
        set(value) {
            iterator.sequence = value
        }

    override fun next(): Int {
        if (isEof()) {
            return if (errorSeek > beginSeek) {
                StepCode.COMPLETE
            } else {
                StepCode.EOF
            }
        }

        val next = iterator.next()
        return when {
            next == StepCode.HAS_NEXT -> StepCode.HAS_NEXT
            next.canComplete() -> {
                if (errorSeek > beginSeek) {
                    iterator.resetSeek(errorSeek)
                    StepCode.COMPLETE
                } else {
                    StepCode.NO_PREDICATE_FAILED
                }
            }
            else -> {
                iterator.resetSeek(iterator.seek)
                errorSeek = iterator.seek
                StepCode.HAS_NEXT_MAY_COMPLETE
            }
        }
    }

    override fun getResult(): CharSequence {
        return getToken()
    }

    override fun prev() {
        errorSeek--
        iterator.resetSeek(errorSeek)
    }

    override val seek: Int
        get() = iterator.seek

    override fun getBeginSeek(): Int {
        return beginSeek
    }

    override fun resetSeek(seek: Int) {
        iterator.resetSeek(seek)
        beginSeek = seek
        errorSeek = seek
    }

    override var skipper: ParseIterator<*>?
        get() = iterator.skipper
        set(value) {
            iterator.skipper = value
        }

    override fun getToken(): CharSequence {
        return sequence.subSequence(beginSeek, errorSeek)
    }
}

class NoRule<T>(
    private val rule: Rule<T>
) : StringRule() {
    override fun createParseIterator(): ParseIterator<CharSequence> {
        return NoIterator(rule.iterator)
    }
}