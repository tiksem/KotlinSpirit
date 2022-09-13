package com.example.kotlinspirit

abstract class BaseRuleWithResult<T : Any>(
    protected val rule: Rule<T>,
    protected val callback: (T) -> Unit
) : Rule<T>() {
    private val result = ParseResult<T>()

    override fun parse(seek: Int, string: CharSequence): Long {
        rule.parseWithResult(seek, string, result)
        if (result.parseResult.getParseCode().isNotError()) {
            callback(result.data ?: throw IllegalStateException("result is null"))
        }

        return result.parseResult
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (result.parseResult.getParseCode().isNotError()) {
            callback(result.data ?: throw IllegalStateException("result is null"))
        }
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return noParse(seek, string)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.hasMatch(seek, string)
    }
}

class RuleWithDefaultRepeatResult<T : Any>(
    rule: RuleWithDefaultRepeat<T>,
    callback: (T) -> Unit
) : BaseRuleWithResult<T>(rule, callback) {
    override fun repeat(): Rule<List<T>> {
        return rule.repeat() as Rule<List<T>>
    }

    override fun repeat(range: IntRange): Rule<*> {
        return rule.repeat(range) as Rule<List<T>>
    }

    override fun invoke(callback: (T) -> Unit): RuleWithDefaultRepeatResult<T> {
        return RuleWithDefaultRepeatResult(rule as RuleWithDefaultRepeat<T>, callback)
    }

    override fun clone(): Rule<T> {
        return RuleWithDefaultRepeatResult((rule as RuleWithDefaultRepeat<T>).clone(), callback)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = rule.debugNameShouldBeWrapped

    override fun debug(name: String?): RuleWithDefaultRepeatResult<T> {
        return RuleWithDefaultRepeatResult(rule.debug(name) as RuleWithDefaultRepeat<T>, callback)
    }
}

class CharPredicateResultRule(
    rule: CharPredicateRule,
    callback: (Char) -> Unit
) : BaseRuleWithResult<Char>(rule, callback) {
    override fun repeat(): StringCharPredicateRule {
        return (rule as CharPredicateRule).repeat()
    }

    override fun repeat(range: IntRange): StringCharPredicateRangeRule {
        return (rule as CharPredicateRule).repeat(range)
    }

    override fun invoke(callback: (Char) -> Unit): CharPredicateResultRule {
        return CharPredicateResultRule(rule as CharPredicateRule, callback)
    }

    override fun not(): CharPredicateRule {
        val rule = rule as CharPredicateRule
        return CharPredicateRule(
            predicate = {
                !rule.predicate(it)
            }
        )
    }

    override fun clone(): CharPredicateResultRule {
        return CharPredicateResultRule((rule as CharPredicateRule).clone(), callback)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = rule.debugNameShouldBeWrapped

    override fun debug(name: String?): CharPredicateResultRule {
        return CharPredicateResultRule(rule.debug(name) as CharPredicateRule, callback)
    }
}