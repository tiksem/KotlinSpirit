package com.example.kotlinspirit

private class AnyStringRuleIterator : BaseStringIterator() {
    override fun next(): Int {
        return if (isEof()) {
            StepCode.COMPLETE
        } else {
            StepCode.HAS_NEXT_MAY_COMPLETE
        }
    }
}

class AnyStringRule : StringRule() {
    override fun createParseIterator(): ParseIterator<CharSequence> {
        return AnyStringRuleIterator()
    }
}