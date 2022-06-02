package com.example.kotlinspirit

abstract class CharRule : BaseRule<Char>()

abstract class BaseCharParseIterator : BaseParseIterator<Char>() {
    override fun getResult(context: ParseContext): Char {
        return context.string[seek - 1]
    }
}

internal open class AnyCharRule : CharRule() {
    override fun createParseIterator(): ParseIterator<Char> {
        return object : BaseCharParseIterator() {
            override fun next(context: ParseContext): Int {
                logNext()
                if (isEof(context)) {
                    return StepCode.EOF
                }

                seek++
                return StepCode.COMPLETE
            }
        }
    }
}

class CharMatchIterator(
    private val predicate: (Char) -> Boolean
) : BaseCharParseIterator() {
    override fun next(context: ParseContext): Int {
        logNext()
        if (isEof(context)) {
            return StepCode.EOF
        }

        val char = context.readChar()
        return if (predicate(char)) {
            StepCode.COMPLETE
        } else {
            StepCode.CHAR_DOES_NOT_MATCH
        }
    }
}

class CharMatchRule(
    private val predicate: (Char) -> Boolean
) : CharRule() {
    override fun createParseIterator(): ParseIterator<Char> {
        return CharMatchIterator(predicate)
    }
}