package com.example.kotlinspirit

open class CharPredicateRule : CharRule {
    internal val predicate: (Char) -> Boolean
    internal val data: CharPredicateData?
    internal val eofParseCode: Int

    internal constructor(
        data: CharPredicateData?,
        predicate: (Char) -> Boolean,
        eofParseCode: Int = ParseCode.EOF
    ) {
        this.data = data
        this.predicate = predicate
        this.eofParseCode = eofParseCode
    }

    internal constructor(data: CharPredicateData, eofParseCode: Int = ParseCode.EOF) {
        this.data = data
        this.predicate = data.toPredicate()
        this.eofParseCode = eofParseCode
    }

    internal constructor(predicate: (Char) -> Boolean, eofParseCode: Int = ParseCode.EOF) {
        this.predicate = predicate
        this.data = null
        this.eofParseCode = eofParseCode
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        if (seek >= string.length) {
            return createStepResult(
                seek = seek,
                parseCode = eofParseCode
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
                parseCode = eofParseCode
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
            },
            eofParseCode = ParseCode.COMPLETE
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
            CharPredicateRule(predicate = {
                thisPredicate(it) || otherPredicate(it)
            })
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
            CharPredicateRule(predicate = {
                thisPredicate(it) && !otherPredicate(it)
            })
        }
    }

    override fun repeat(): StringCharPredicateRule {
        return StringCharPredicateRule(predicate)
    }

    override fun repeat(range: IntRange): StringCharPredicateRangeRule {
        return StringCharPredicateRangeRule(predicate, range)
    }

    override fun unaryPlus(): StringOneOrMoreCharPredicateRule {
        return StringOneOrMoreCharPredicateRule(predicate)
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
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    private fun generateDebugName(): String {
        val data = data
        if (data != null) {
            val chars = if (data.chars.isEmpty()) {
                ""
            } else {
                "(" + data.chars.joinToString(",") {
                    if (it == ',') "`,`" else it.toString()
                } + ")"
            }
            val ranges = data.ranges.joinToString("") {
                "[${it.first}..${it.last}]"
            }
            return "char$chars$ranges"
        } else {
            return "charWithCustomPredicate"
        }
    }

    override fun debug(name: String?): CharPredicateRule {
        return DebugCharPredicateRule(
            name = name ?: generateDebugName(),
            data = data,
            predicate = predicate,
            eofParseCode = eofParseCode
        )
    }
}

private class DebugCharPredicateRule(
    override val name: String,
    data: CharPredicateData?,
    predicate: (Char) -> Boolean,
    eofParseCode: Int
) : CharPredicateRule(data, predicate, eofParseCode), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}
