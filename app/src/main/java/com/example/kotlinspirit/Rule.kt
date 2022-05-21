package com.example.kotlinspirit

interface Rule<T> {
    fun parse(state: ParseState)
    fun getResult(state: ParseState): T

    fun tryParse(string: String): T? {
        val state = string.toParseState()
        parse(state)
        return if (state.hasError) {
            null
        } else {
            getResult(state)
        }
    }

    fun match(string: String): Boolean {
        val state = string.toParseState()
        parse(state)
        return !state.hasError && state.seek == state.array.size
    }

    fun parseOrThrow(state: ParseState): T {
        parse(state)
        if (state.hasError) {
            throw ParseException(
                state = state,
                tokenName = javaClass.name
            )
        }

        return getResult(state)
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
}