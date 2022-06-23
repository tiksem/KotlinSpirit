package com.example.kotlinspirit

abstract class BaseRuleWithResult<T : Any>(
    protected val rule: Rule<T>,
    protected val callback: (T) -> Unit
) : Rule<T>() {
    private val result = ParseResult<T>()

    override fun parse(seek: Int, string: CharSequence): Long {
        rule.parseWithResult(seek, string, result)
        if (result.stepResult.getStepCode().isNotError()) {
            callback(result.data ?: throw IllegalStateException("result is null"))
        }

        return result.stepResult
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        rule.parseWithResult(seek, string, result)
        if (result.stepResult.getStepCode().isNotError()) {
            callback(result.data ?: throw IllegalStateException("result is null"))
        }
    }

    override fun notifyParseStepComplete(string: CharSequence) {
        callback(getStepParserResult(string))
    }

    override fun resetStep() {
        rule.resetStep()
    }

    override fun getStepParserResult(string: CharSequence): T {
        return rule.getStepParserResult(string)
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        return rule.parseStep(seek, string).also {
            if (it.getStepCode() == StepCode.COMPLETE) {
                callback(getStepParserResult(string))
            }
        }
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        return noParse(seek, string)
    }

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        return rule.noParseStep(seek, string)
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

    override fun clone(): RuleWithDefaultRepeatResult<T> {
        return RuleWithDefaultRepeatResult(rule.clone() as RuleWithDefaultRepeat<T>, callback)
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

    override fun clone(): CharPredicateResultRule {
        return CharPredicateResultRule(rule.clone() as CharPredicateRule, callback)
    }
}