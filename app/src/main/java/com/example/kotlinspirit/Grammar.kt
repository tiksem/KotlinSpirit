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

    override fun resetStep() {
        resetResult()
        r?.resetStep()
    }

    override fun getStepParserResult(string: CharSequence): T {
        return result
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        return initRule().parseStep(seek, string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return initRule().noParse(seek, string)
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        return initRule().noParseStep(seek, string)
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        resetResult()
        return initRule().parse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        resetResult()
        val resultSeek = initRule().parse(seek, string)
        result.stepResult = resultSeek
        if (resultSeek >= 0) {
            result.data = this.result
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return initRule().hasMatch(seek, string)
    }

    override fun resetNoStep() {
        initRule().resetNoStep()
    }

    override fun clone(): Grammar<T> {
        return this.javaClass.newInstance()
    }
}