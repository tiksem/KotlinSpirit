package com.example.kotlinspirit

private class ExactStringRuleIterator(
    private val value: CharSequence
) : BaseParseIterator<CharSequence>() {
    override fun getResult(): CharSequence {
        return value
    }

    override fun next(): Int {
        val i = seek - seekBegin
        if (isEof()) {
            return if (i == value.length) {
                StepCode.COMPLETE
            } else {
                StepCode.EOF
            }
        }

        if (i >= value.length) {
            return StepCode.COMPLETE
        }

        return if (readChar() == value[i]) {
            StepCode.HAS_NEXT
        } else {
            StepCode.STRING_DOES_NOT_MATCH
        }
    }

}

internal class ExactStringRule(
    private val value: CharSequence
) : StringRule() {
    override fun createParseIterator(): ParseIterator<CharSequence> {
        return ExactStringRuleIterator(value)
    }
}

fun str(string: CharSequence): StringRule {
    return ExactStringRule(string)
}