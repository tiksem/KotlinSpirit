package com.example.kotlinspirit

abstract class Grammar<T : Any> : RuleWithDefaultRepeat<T>() {
    private var r: Rule<*>? = null

    abstract val result: T
    protected abstract fun defineRule(): Rule<*>
    protected open fun resetResult() {}

    protected fun initRule(): Rule<*> {
        var rule = this.r
        if (rule == null) {
            rule = defineRule()
            this.r = rule
        }

        return rule
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return initRule().noParse(seek, string)
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        return initRule().parse(seek, string).also {
            resetResult()
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val resultSeek = initRule().parse(seek, string)
        result.parseResult = resultSeek
        if (resultSeek >= 0) {
            result.data = this.result
        }
        resetResult()
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return initRule().hasMatch(seek, string)
    }

    override fun clone(): Grammar<T> {
        return javaClass.newInstance()
    }

    fun recursive(): RecursiveGrammar<T> {
        return RecursiveGrammar(this)
    }
}

class RecursiveGrammar<T : Any>(private val grammar: Grammar<T>) : RuleWithDefaultRepeat<T>() {
    private val stack = arrayListOf(grammar)
    private var stackSeek = 0

    private fun getGrammar(): Grammar<T> {
        return if (stackSeek == stack.size) {
            grammar.clone().also {
                stack.add(it)
            }
        } else {
            stack[stackSeek]
        }.also {
            stackSeek++
        }
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        return getGrammar().parse(seek, string).also {
            --stackSeek
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        return getGrammar().parseWithResult(seek, string, result).also {
            --stackSeek
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return grammar.hasMatch(seek, string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return grammar.noParse(seek, string)
    }

    override fun clone(): RuleWithDefaultRepeat<T> {
        return RecursiveGrammar(grammar.clone())
    }
}