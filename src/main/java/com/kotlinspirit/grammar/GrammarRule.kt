package com.kotlinspirit.grammar

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

open class GrammarRule<T : Any>(
    private val grammar: Grammar<T>,
    name: String?
): RuleWithDefaultRepeat<T>(name) {
    private val stack = arrayListOf(grammar)
    private var stackSeek = 0

    private fun pullGrammar(): Grammar<T> {
        return if (stack.size > stackSeek) {
            stack[stackSeek++]
        } else {
            grammar.clone().also {
                stack.add(it)
                stackSeek++
            }
        }
    }

    private fun returnGrammar(grammar: Grammar<T>) {
        grammar.resetResult()
        stackSeek--
    }

    private inline fun baseParse(
        seek: Int,
        string: CharSequence,
        parser: Rule<*>.(Int, CharSequence) -> ParseSeekResult
    ): ParseSeekResult {
        val grammar = pullGrammar()
        try {
            return grammar.initRule().parser(seek, string)
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
            val parseResult = grammar.initRule().parser(seek, string)
            result.parseResult = parseResult
            if (parseResult.isComplete) {
                result.data = grammar.result
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
        TODO("Not yet implemented")
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return pullGrammar().initRule().hasMatch(seek, string)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "grammar"

    override fun clone(): GrammarRule<T> {
        return GrammarRule(grammar.clone(), name)
    }

    override fun debug(engine: DebugEngine): DebugRule<T> {
        return DebugRule(
            rule = GrammarRule(DebugGrammar(grammar.clone(), engine), name),
            engine = engine
        )
    }

    override fun name(name: String): GrammarRule<T> {
        return GrammarRule(grammar, name)
    }

    override fun isThreadSafe(): Boolean {
        return false
    }
}