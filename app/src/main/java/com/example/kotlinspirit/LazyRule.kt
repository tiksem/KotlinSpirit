package com.example.kotlinspirit

abstract class BaseLazyRule<T : Any>(
    private val ruleProvider: () -> Rule<T>
): Rule<T>() {
    private var rule: Rule<T>? = null

    protected open fun initRule(): Rule<T> {
        val rule = this.rule
        if (rule != null) {
            return rule
        }

        return ruleProvider().also {
            this.rule = it
        }
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        return initRule().parse(seek, string)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        initRule().parseWithResult(seek, string, result)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return initRule().hasMatch(seek, string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return initRule().noParse(seek, string)
    }
}

class LazyCharPredicateRule(
    private val ruleProvider: () -> CharPredicateRule
) : BaseLazyRule<Char>(ruleProvider) {
    override fun repeat(): StringCharPredicateRule {
        return StringCharPredicateRule((initRule() as CharPredicateRule).predicate)
    }

    override fun clone(): LazyCharPredicateRule {
        return LazyCharPredicateRule(ruleProvider)
    }

    override fun repeat(range: IntRange): Rule<*> {
        return initRule().repeat(range)
    }

    override fun invoke(callback: (Char) -> Unit): BaseRuleWithResult<Char> {
        return initRule().invoke(callback)
    }
}

class LazyRule<T : Any>(
    private val ruleProvider: () -> RuleWithDefaultRepeat<T>
) : BaseLazyRule<T>(ruleProvider) {
    override fun initRule(): RuleWithDefaultRepeat<T> {
        return super.initRule() as RuleWithDefaultRepeat<T>
    }

    override fun repeat(): Rule<List<T>> {
        return ZeroOrMoreRule(this)
    }

    override fun clone(): Rule<T> {
        return LazyRule(ruleProvider)
    }

    override fun repeat(range: IntRange): Rule<List<T>> {
        return initRule().repeat(range)
    }

    override fun invoke(callback: (T) -> Unit): BaseRuleWithResult<T> {
        return RuleWithDefaultRepeatResult(initRule(), callback)
    }
}