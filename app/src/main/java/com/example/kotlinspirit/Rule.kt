package com.example.kotlinspirit

interface Rule<T> {
    fun parse(state: ParseState, requireResult: Boolean = false)
    fun getResult(array: CharArray, seekBegin: Int, seekEnd: Int): T

    fun getResult(state: ParseState): T {
        return getResult(state.array, state.seekTokenBegin, state.seek)
    }

    fun tryParse(string: String): T? {
        val state = string.toParseState()
        parse(state, true)
        return if (state.hasError) {
            null
        } else {
            getResult(state.array, state.seekTokenBegin, state.seek)
        }
    }

    fun match(string: String): Boolean {
        val state = string.toParseState()
        parse(state, false)
        return !state.hasError && state.seek == state.array.size
    }

    fun parseOrThrow(state: ParseState): T {
        parse(state, true)
        if (state.hasError) {
            throw ParseException(
                state = state,
                tokenName = javaClass.name
            )
        }

        return getResult(state)
    }

    fun parseOrThrow(string: String): T {
        return parseOrThrow(string.toParseState())
    }

    fun on(
        error: (() -> Unit)? = null,
        success: (T) -> Unit
    ): Rule<T> {
        return RuleParseCallbackWrapper(
            rule = this,
            callback = success,
            errorCallback = error
        )
    }

    operator fun plus(rule: Rule<*>): SequenceRule {
        return SequenceRule(this, rule)
    }

    operator fun plus(value: Int): SequenceRule {
        return SequenceRule(this, int(value))
    }

    operator fun plus(value: String): SequenceRule {
        return SequenceRule(this, str(value))
    }

    operator fun plus(value: Char): SequenceRule {
        return SequenceRule(this, char(value))
    }

    infix fun or(rule: Rule<*>): Rule<Any> {
        return OrRule(this as Rule<Any>, rule as Rule<Any>)
    }

    infix fun or(value: Int): Rule<Any> {
        return or(int(value))
    }

    infix fun or(value: String): Rule<Any> {
        return or(str(value))
    }

    infix fun or(value: Char): Rule<Any> {
        return or(char(value))
    }

    fun repeatAtLeast(times: Int): RepeatRule<T> {
        return RepeatRule(
            minimumLength = times,
            maximumLength = Int.MAX_VALUE,
            rule = this
        )
    }

    fun repeat(): RepeatRule<T> {
        return RepeatRule(
            minimumLength = 0,
            maximumLength = Int.MAX_VALUE,
            rule = this
        )
    }

    fun repeat(range: IntRange): RepeatRule<T> {
        return RepeatRule(
            minimumLength = range.first,
            maximumLength = range.last,
            rule = this
        )
    }

    operator fun minus(rule: Rule<*>): DifferenceRule<T> {
        return DifferenceRule(
            main = this,
            exception = rule
        )
    }

    operator fun not(): StringRule {
        return NoRule(this)
    }

    fun split(
        divider: Rule<*>,
        min: Int = 0,
        max: Int = Int.MAX_VALUE
    ): SplitRule<T> {
        return SplitRule(
            minimumLength = min,
            maximumLength = max,
            tokenRule = this,
            dividerRule = divider
        )
    }

    fun split(
        divider: Char,
        min: Int = 0,
        max: Int = Int.MAX_VALUE
    ): SplitRule<T> {
        return SplitRule(
            minimumLength = min,
            maximumLength = max,
            tokenRule = this,
            dividerRule = char(divider)
        )
    }

    fun split(
        divider: String,
        min: Int = 0,
        max: Int = Int.MAX_VALUE
    ): SplitRule<T> {
        return SplitRule(
            minimumLength = min,
            maximumLength = max,
            tokenRule = this,
            dividerRule = str(divider)
        )
    }

    operator fun rem(divider: Rule<*>): SplitRule<T> {
        return split(divider)
    }

    operator fun rem(divider: Char): SplitRule<T> {
        return split(divider)
    }

    operator fun rem(divider: String): SplitRule<T> {
        return split(divider)
    }
}