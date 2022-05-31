package com.example.kotlinspirit

private class NoIterator<T>(
    private val iterator: ParseIterator<T>
) : ParseIterator<CharSequence> {
    private var beginSeek = iterator.seek
    private var errorSeek: Int = beginSeek

    override fun next(context: ParseContext): Int {
        if (isEof(context)) {
            return if (errorSeek > beginSeek) {
                StepCode.COMPLETE
            } else {
                StepCode.EOF
            }
        }

        val next = iterator.next(context)
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

    override fun getResult(context: ParseContext): CharSequence {
        return getToken(context)
    }

    override fun prev(context: ParseContext) {
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
}

class NoRule<T>(
    private val rule: Rule<T>
) : StringRule() {
    override fun createParseIterator(): ParseIterator<CharSequence> {
        return NoIterator(rule.iterator)
    }
}