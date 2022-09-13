package com.example.kotlinspirit

open class StringOneOrMoreCharPredicateRule(
    private val predicate: (Char) -> Boolean
) : RuleWithDefaultRepeat<CharSequence>() {

    override fun parse(seek: Int, string: CharSequence): Long {
        var i = seek
        while (i < string.length) {
            val c = string[i]
            if (!predicate(c)) {
                return if (i - seek >= 1) {
                    createComplete(i)
                } else {
                    createStepResult(
                        seek = i,
                        ParseCode.STRING_NOT_ENOUGH_DATA
                    )
                }
            }

            i++
        }

        return if (i - seek >= 1) {
            createComplete(i)
        } else {
            createStepResult(
                seek = i,
                ParseCode.STRING_NOT_ENOUGH_DATA
            )
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        var i = seek
        while (i < string.length) {
            val c = string[i]
            if (!predicate(c)) {
                if (i - seek >= 1) {
                    result.data = string.subSequence(seek, i)
                    result.parseResult = createComplete(i)
                } else {
                    result.parseResult = createStepResult(
                        seek = i,
                        ParseCode.STRING_NOT_ENOUGH_DATA
                    )
                }
                return
            }

            i++
        }

        if (i - seek >= 1) {
            result.data = string.subSequence(seek, i)
            result.parseResult = createComplete(i)
        } else {
            result.parseResult = createStepResult(
                seek = i,
                ParseCode.STRING_NOT_ENOUGH_DATA
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < string.length && predicate(string[seek])
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        val length = string.length
        if (seek >= length) {
            return seek
        }

        var i = seek
        do {
            val c = string[i]
            if (predicate(c)) {
                return if (i == seek) {
                    -i - 1
                } else {
                    i
                }
            }

            i++
        } while (i < length)

        return i
    }

    override fun clone(): StringOneOrMoreCharPredicateRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun debug(name: String?): StringOneOrMoreCharPredicateRule {
        return DebugStringOneOrMoreCharPredicateRule(name ?: "stringPredicate[1..<]", predicate)
    }
}

private class DebugStringOneOrMoreCharPredicateRule(
    override val name: String,
    predicate: (Char) -> Boolean
) : StringOneOrMoreCharPredicateRule(predicate), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<CharSequence>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}