package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char

operator fun Char.plus(rule: Rule<*>): SequenceRule {
    return ExactCharRule(this) + rule
}

operator fun Char.plus(rule: CharRule): StringRuleWrapper {
    return (ExactCharRule(this) + rule).asString()
}

operator fun Char.plus(rule: OptionalCharRule): StringRuleWrapper {
    return (ExactCharRule(this) + rule).asString()
}

infix fun Char.or(rule: Rule<*>): OrRule<*> {
    return ExactCharRule(this) or rule
}

infix fun Char.or(rule: CharPredicateRule): CharPredicateRule {
    return char(this) or rule
}

fun Char.expect(rule: Rule<*>): ExpectationRule<Char> {
    return ExpectationRule(ExactCharRule(this), rule)
}

infix fun String.or(rule: ExactStringRule): OneOfStringRule {
    return ExactStringRule(this) or rule
}

infix fun String.or(rule: String): OneOfStringRule {
    return ExactStringRule(this) or rule
}

infix fun String.or(rule: Rule<*>): AnyOrRule {
    return ExactStringRule(this) or rule
}

fun String.expect(rule: Rule<*>): ExpectationRule<CharSequence> {
    return ExpectationRule(ExactStringRule(this), rule)
}