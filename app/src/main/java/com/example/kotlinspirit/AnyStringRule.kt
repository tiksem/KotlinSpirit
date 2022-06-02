package com.example.kotlinspirit

import android.util.Log

private class AnyStringRuleIterator : BaseStringIterator() {
    override fun next(context: ParseContext): Int {
        return if (isEof(context)) {
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