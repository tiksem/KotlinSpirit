package com.example.kotlinspirit

private class NoIterator<T>(
    private val iterator: ParseIterator<T>
) : ParseIterator<CharSequence> by iterator as ParseIterator<CharSequence> {
    override fun next(): Int {
        return when (iterator.next()) {
            StepCode.HAS_NEXT -> StepCode.HAS_NEXT
            StepCode.HAS_NEXT_MAY_COMPLETE -> StepCode.NO_PREDICATE_FAILED
            StepCode.COMPLETE -> StepCode.NO_PREDICATE_FAILED
            else -> StepCode.COMPLETE
        }
    }

    override fun getResult(): CharSequence {
        return iterator.getToken()
    }
}

class NoRule<T>(
    private val rule: Rule<T>
) : StringRule() {
    override fun createParseIterator(): ParseIterator<CharSequence> {
        return NoIterator(rule.iterator)
    }
}