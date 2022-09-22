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

open class RuleWithDefaultRepeatResult<T : Any>(
    rule: RuleWithDefaultRepeat<T>,
    callback: (T) -> Unit
) : BaseRuleWithResult<T>(rule, callback) {
    override fun repeat(): Rule<List<T>> {
        return rule.repeat() as Rule<List<T>>
    }

    override fun repeat(range: IntRange): Rule<List<T>> {
        return rule.repeat(range) as Rule<List<T>>
    }

    override fun unaryPlus(): Rule<List<T>> {
        return +rule as Rule<List<T>>
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
        return DebugRuleWithDefaultRepeatResult(rule.debug(name) as RuleWithDefaultRepeat<T>, callback)
    }
}

private class DebugRuleWithDefaultRepeatResult<T : Any>(
    rule: RuleWithDefaultRepeat<T>,
    callback: (T) -> Unit
) : RuleWithDefaultRepeatResult<T>(rule, callback), DebugRule {
    override val name: String
        get() = rule.debugName
}

open class CharPredicateResultRule(
    rule: CharPredicateRule,
    callback: (Char) -> Unit
) : BaseRuleWithResult<Char>(rule, callback) {
    override fun repeat(): StringCharPredicateRule {
        return (rule as CharPredicateRule).repeat()
    }

    override fun repeat(range: IntRange): StringCharPredicateRangeRule {
        return (rule as CharPredicateRule).repeat(range)
    }

    override fun unaryPlus(): StringOneOrMoreCharPredicateRule {
        return +(rule as CharPredicateRule)
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
        return DebugCharPredicateResultRule(rule.debug(name) as CharPredicateRule, callback)
    }
}

private class DebugCharPredicateResultRule(
    rule: CharPredicateRule,
    callback: (Char) -> Unit
) : CharPredicateResultRule(rule, callback), DebugRule {
    override val name: String
        get() = rule.debugName
}