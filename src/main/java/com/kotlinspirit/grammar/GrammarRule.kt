package com.kotlinspirit.grammar

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.getParseCode
import com.kotlinspirit.core.isNotError
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

open class GrammarRule<T : Any>(
    private val grammar: Grammar<T>,
    name: String?
): RuleWithDefaultRepeat<T>(name) {
    private var ruleForParse: Rule<*>? = null
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

    private fun initRuleForParse(): Rule<*> {
        var r = ruleForParse
        if (r == null) {
            r = grammar.initRule().ignoreCallbacks()
            ruleForParse = r
        }

        return r
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        return initRuleForParse().parse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val grammar = pullGrammar()
        try {
            val parseResult = grammar.initRule().parse(seek, string)
            result.parseResult = parseResult
            if (parseResult.getParseCode().isNotError()) {
                result.data = grammar.result
            }
        } finally {
            returnGrammar(grammar)
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return initRuleForParse().hasMatch(seek, string)
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

    override fun ignoreCallbacks(): GrammarRule<T> {
        return this
    }
}