package com.kotlinspirit.grammar

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isNotError
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

open class GrammarRule<T : Any>(private val grammar: Grammar<T>): RuleWithDefaultRepeat<T>() {
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

    override fun parse(seek: Int, string: CharSequence): Long {
        val grammar = pullGrammar()
        return grammar.initRule().parse(seek, string).also {
            returnGrammar(grammar)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val grammar = pullGrammar()
        val parseResult = grammar.initRule().parse(seek, string)
        result.parseResult = parseResult
        if (parseResult.getParseCode().isNotError()) {
            result.data = grammar.result
        }
        returnGrammar(grammar)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return grammar.initRule().hasMatch(seek, string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return grammar.initRule().noParse(seek, string)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override fun clone(): GrammarRule<T> {
        return GrammarRule(grammar.clone())
    }

    override fun debug(name: String?): GrammarRule<T> {
        return DebugGrammarRule(name ?: grammar.name, grammar)
    }
}

private class DebugGrammarRule<T : Any>(
    override val name: String,
    grammar: Grammar<T>
) : GrammarRule<T>(grammar), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, r: ParseResult<T>) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, r)
        DebugEngine.ruleParseEnded(this, r.parseResult)
    }
}