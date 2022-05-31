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

            override fun parse(context: ParseContext): ParseResult<To> {
                return this@Rule.parse(context).let {
                    ParseResult(
                        seek = it.seek,
                        code = it.code,
                        result = it.result?.let(func)
                    )
                }
            }
        }
    }

    val iterator: ParseIterator<T>

    fun parse(
        context: ParseContext
    ): ParseResult<T>

    fun parse(
        string: CharSequence,
        skipper: Rule<*>? = null
    ): ParseResult<T> {
        return parse(ParseContext(string, skipper))
    }

    fun tryParse(string: String): T? {
        return parse(string).result
    }

    fun match(string: String): Boolean {
        return parse(string).seek == string.length
    }

    fun parseOrThrow(string: CharSequence, skipper: Rule<*>? = null): T {
        val result = parse(string, skipper)
        if (result.hasError) {
            throw ParseException(
                string = string,
                seek = result.seek,
                errorCode = result.code
            )
        }

        return result.result ?: throw IllegalStateException("Undefined behaviour")
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
        range: IntRange,
    ): SplitRule<T> {
        return SplitRule(
            range = range,
            tokenRule = this,
            dividerRule = divider
        )
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
        divider: String,
        range: IntRange
    ): SplitRule<T> {
        return split(
            divider = str(divider),
            range = range
        )
    }

    fun split(
        divider: Char,
        range: IntRange
    ): SplitRule<T> {
        return split(
            divider = char(divider),
            range = range
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

    override val iterator: ParseIterator<T> by lazy {
        createParseIterator()
    }

    override fun parse(
        context: ParseContext
    ): ParseResult<T> {
        val iter = iterator
        iter.resetSeek(0)
        val seek = iter.skip(context)
        iter.resetSeek(seek)

        while (true) {
            val code = iter.next(context)
            if (!code.hasNext()) {
                return if (code == StepCode.COMPLETE) {
                    ParseResult.result(
                        seek = iter.seek,
                        result = iter.getResult(context),
                    )
                } else {
                    ParseResult.error(
                        seek = iter.seek,
                        errorCode = code
                    )
                }
            }
        }
    }
}