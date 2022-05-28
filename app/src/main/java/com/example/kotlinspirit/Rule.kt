package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.int
import com.example.kotlinspirit.Rules.str
import java.lang.IllegalStateException

interface Rule<T> {
    fun <To> transform(func: (T) -> To): Rule<To> {
        return object : Rule<To> {
            override val iterator: ParseIterator<To>
                get() = this@Rule.iterator.transform(func)

            override fun parse(
                state: ParseState,
                string: CharSequence,
                requireResult: Boolean,
                maxLength: Int?
            ): To? {
                return this@Rule.parse(state, string, requireResult, maxLength)?.let {
                    func(it)
                }
            }
        }
    }

    val iterator: ParseIterator<T>

    fun parse(
        state: ParseState,
        string: CharSequence,
        requireResult: Boolean = false,
        maxLength: Int? = null
    ): T?

    fun tryParse(string: String): T? {
        val state = ParseState()
        return parse(state, string, true)
    }

    fun match(string: String): Boolean {
        val state = ParseState()
        parse(state, string, false)
        return !state.hasError && state.seek == string.length
    }

    fun parseOrThrow(string: String): T {
        val state = ParseState()
        val result = parse(state, string, true)
        if (state.hasError) {
            throw ParseException(
                string = string,
                state = state,
                tokenName = javaClass.name
            )
        }

        return result ?: throw IllegalStateException("Undefined behaviour")
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

    fun repeat(times: Int): RepeatRule<T> {
        return repeat(range = times..times)
    }

    fun repeatAtLeast(times: Int): RepeatRule<T> {
        return repeat(range = times..Int.MAX_VALUE)
    }

    fun repeat(): RepeatRule<T> {
        return repeat(range = 0..Int.MAX_VALUE)
    }

    fun repeat(range: IntRange): RepeatRule<T> {
        return RepeatRule(
            range = range,
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
        count: Int,
    ): SplitRule<T> {
        return SplitRule(
            range = count..count,
            tokenRule = this,
            dividerRule = divider
        )
    }

    fun split(
        divider: Char,
        count: Int,
    ): SplitRule<T> {
        return split(
            divider = char(divider),
            count = count
        )
    }

    fun split(
        divider: String,
        count: Int
    ): SplitRule<T> {
        return split(
            divider = str(divider),
            count = count
        )
    }

    fun split(
        divider: Rule<*>
    ): SplitRule<T> {
        return SplitRule(
            range = 0..Int.MAX_VALUE,
            tokenRule = this,
            dividerRule = divider
        )
    }

    fun split(
        divider: Char
    ): SplitRule<T> {
        return split(
            char(divider)
        )
    }

    fun split(
        divider: String
    ): SplitRule<T> {
        return split(
            str(divider)
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

abstract class BaseRule<T>: Rule<T> {
    abstract fun createParseIterator(): ParseIterator<T>

    protected open fun checkPrecondition(string: CharSequence, state: ParseState) {
    }
    protected open fun checkPostCondition(string: CharSequence, state: ParseState) {
    }

    override val iterator: ParseIterator<T> by lazy {
        createParseIterator()
    }

    override fun parse(
        state: ParseState,
        string: CharSequence,
        requireResult: Boolean,
        maxLength: Int?
    ): T? {
        checkPrecondition(string, state)
        if (state.hasError) {
            return null
        }

        val iter = iterator
        state.startParseToken()
        iter.setSequence(
            string = string,
            length = maxLength ?: string.length
        )
        iter.resetSeek(state.seek)
        while (true) {
            val code = iter.next()
            if (!code.hasNext()) {
                state.seek = iter.seek
                state.parseCode = code
                return if (code == StepCode.COMPLETE) {
                    if (requireResult) {
                        checkPostCondition(string, state)
                        if (state.hasError) {
                            null
                        } else {
                            iter.getResult()
                        }
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        }
    }
}