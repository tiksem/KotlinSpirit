package com.example.kotlinspirit

class StringCharPredicateRule(
    private val predicate: (Char) -> Boolean
) : RuleWithDefaultRepeat<CharSequence>() {
    override fun parse(seek: Int, string: CharSequence): Long {
        var i = seek
        while (i < string.length) {
            val c = string[i]
            if (!predicate(c)) {
                return createComplete(i)
            }

            i++
        }

        return createComplete(i)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<CharSequence>) {
        var i = seek
        while (i < string.length) {
            val c = string[i]
            if (!predicate(c)) {
                result.data = string.subSequence(seek, i)
                result.parseResult = createComplete(i)
                return
            }

            i++
        }

        result.data = string.subSequence(seek, i)
        result.parseResult = createComplete(i)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return true
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        var i = seek
        val length = string.length
        while (i < length) {
            val c = string[i]
            if (predicate(c)) {
                break
            }

            i++
        }

        return if (i == seek) {
            -seek - 1
        } else {
            i
        }
    }

    override fun clone(): StringCharPredicateRule {
        return this
    }
}