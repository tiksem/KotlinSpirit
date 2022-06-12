package com.example.kotlinspirit

abstract class Grammar<T : Any> : Rule<T> {
    private var rule: Rule<*>? = null

    abstract val result: T
    protected abstract fun defineRule(): Rule<*>
    protected abstract fun resetResult()

    private fun initRule(): Rule<*> {
        var rule = this.rule
        if (rule == null) {
            rule = defineRule()
            this.rule = rule
        }

        return rule
    }

    override fun resetStep() {
        rule?.resetStep()
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

    override fun parse(seek: Int, string: CharSequence): Int {
        return initRule().parse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        val resultSeek = initRule().parse(seek, string)
        result.errorCodeOrSeek = resultSeek
        if (resultSeek >= 0) {
            resetResult()
            result.data = this.result
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return initRule().hasMatch(seek, string)
    }

    override fun resetNoStep() {
        initRule().resetNoStep()
    }
}