package com.kotlinspirit.grammar

import com.kotlinspirit.core.Clearable
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

private data class Grammar<Data>(
    var rule: Rule<*>?,
    val data: Data
) {
    fun defineRule(defineRule: (data: Data) -> Rule<*>): Rule<*> {
        if (rule == null) {
            rule = defineRule(data)
        }

        return rule!!
    }
}

open class GrammarRule<T : Any, Data>(
    private val dataFactory: () -> Data,
    private val defineRule: (data: Data) -> Rule<*>,
    private val getResult: (data: Data) -> T,
    name: String?
): RuleWithDefaultRepeat<T>(name) {
    private val stack = arrayListOf<Grammar<Data>>()
    private var stackSeek = 0

    init {
        val data = dataFactory()
        stack.add(Grammar(null, data))
    }

    private fun pullGrammar(): Grammar<Data> {
        return if (stack.size > stackSeek) {
            stack[stackSeek++]
        } else {
            val data = dataFactory()
            val rule = defineRule(data)
            val element = Grammar(rule, data)
            stackSeek++
            stack.add(element)
            element
        }
    }

    private fun returnGrammar(grammar: Grammar<Data>) {
        (grammar.data as? Clearable)?.clear()
        stackSeek--
    }

    private inline fun baseParse(
        seek: Int,
        string: CharSequence,
        parser: Rule<*>.(Int, CharSequence) -> ParseSeekResult
    ): ParseSeekResult {
        val grammar = pullGrammar()
        try {
            return grammar.defineRule(defineRule).parser(seek, string)
        } finally {
            returnGrammar(grammar)
        }
    }

    private inline fun baseParseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<T>,
        parser: Rule<*>.(Int, CharSequence) -> ParseSeekResult
    ) {
        val grammar = pullGrammar()
        try {
            val parseResult = grammar.defineRule(defineRule).parser(seek, string)
            result.parseResult = parseResult
            if (parseResult.isComplete) {
                result.data = getResult(grammar.data)
            }
        } finally {
            returnGrammar(grammar)
        }
    }

    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return baseParse(seek, string, Rule<*>::parse)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        baseParseWithResult(
            seek = seek,
            string = string,
            result = result,
            parser = Rule<*>::parse
        )
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return baseParse(seek, string, Rule<*>::reverseParse)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        baseParseWithResult(
            seek = seek,
            string = string,
            result = result,
            parser = Rule<*>::reverseParse
        )
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        val grammar = pullGrammar()
        return try {
            grammar.defineRule(defineRule).reverseHasMatch(seek, string)
        } finally {
            returnGrammar(grammar)
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        val grammar = pullGrammar()
        return try {
            grammar.defineRule(defineRule).hasMatch(seek, string)
        } finally {
            returnGrammar(grammar)
        }
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "grammar"

    override fun clone(): GrammarRule<T, Data> {
        return GrammarRule(dataFactory, defineRule, getResult, name)
    }

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = GrammarRule(
                dataFactory = dataFactory,
                defineRule = {
                    defineRule(it).debug(engine)
                },
                getResult,
                name = name
            ).debug(engine),
            engine = engine
        )
    }

    override fun name(name: String): GrammarRule<T, Data> {
        return GrammarRule(dataFactory, defineRule, getResult, name)
    }

    override fun isThreadSafe(): Boolean {
        return false
    }
}