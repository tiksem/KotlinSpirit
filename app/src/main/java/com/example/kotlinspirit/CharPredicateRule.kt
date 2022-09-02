package com.example.kotlinspirit

class CharPredicateRule : CharRule {
    internal val predicate: (Char) -> Boolean
    internal val data: CharPredicateData?

    private constructor(data: CharPredicateData?, predicate: (Char) -> Boolean) {
        this.data = data
        this.predicate = predicate
    }

    internal constructor(data: CharPredicateData) {
        this.data = data
        this.predicate = data.toPredicate()
    }

    internal constructor(predicate: (Char) -> Boolean) {
        this.predicate = predicate
        this.data = null
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        if (seek >= string.length) {
            return createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
        }

        return if (predicate(string[seek])) {
            createComplete(seek + 1)
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        if (seek >= string.length) {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.EOF
            )
            return
        }

        val ch = string[seek]
        if (predicate(ch)) {
            result.data = ch
            result.parseResult = createComplete(seek + 1)
        } else {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.CHAR_PREDICATE_FAILED
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return seek < string.length && predicate(string[seek])
    }

    override fun not(): CharPredicateRule {
        val predicate = this.predicate
        return CharPredicateRule(
            predicate = {
                !predicate(it)
            }
        )
    }

    infix fun or(rule: CharPredicateRule): CharPredicateRule {
        val data = this.data
        val otherData = rule.data

        return if (data != null && otherData != null) {
            CharPredicateRule(data + otherData)
        } else {
            val thisPredicate = this.predicate
            val otherPredicate = rule.predicate
            CharPredicateRule {
                thisPredicate(it) || otherPredicate(it)
            }
        }
    }

    operator fun minus(rule: CharPredicateRule): CharPredicateRule {
        val data = this.data
        val otherData = rule.data

        return if (data != null && otherData != null) {
            CharPredicateRule(data - otherData)
        } else {
            val otherPredicate = rule.predicate
            val thisPredicate = this.predicate
            CharPredicateRule {
                thisPredicate(it) && !otherPredicate(it)
            }
        }
    }

    override fun repeat(): StringCharPredicateRule {
        return StringCharPredicateRule(predicate)
    }

    override fun repeat(range: IntRange): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(predicate, range)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        if (seek >= string.length) {
            return -seek - 1
        }

        return if (!predicate(string[seek])) {
            seek + 1
        } else {
            -seek - 1
        }
    }

    override fun invoke(callback: (Char) -> Unit): CharPredicateResultRule {
        return CharPredicateResultRule(rule = this, callback)
    }

    override fun clone(): CharPredicateRule {
        return CharPredicateRule(data, predicate)
    }
}