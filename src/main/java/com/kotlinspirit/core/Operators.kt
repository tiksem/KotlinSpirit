package com.kotlinspirit.core

import com.kotlinspirit.*
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.char.CharPredicateRule
import com.kotlinspirit.char.CharRule
import com.kotlinspirit.char.ExactCharRule
import com.kotlinspirit.expressive.*
import com.kotlinspirit.str.ExactStringRule
import com.kotlinspirit.str.oneof.OneOfStringRule

operator fun Char.plus(rule: Rule<*>): SequenceRule {
    return ExactCharRule(this) + rule
}

operator fun Char.plus(rule: CharRule): StringRuleWrapper {
    return (ExactCharRule(this) + rule).asString()
}

infix fun Char.or(rule: Rule<*>): OrRule<*> {
    return ExactCharRule(this) or rule
}

infix fun Char.or(rule: CharPredicateRule): CharPredicateRule {
    return char(this) or rule
}

fun Char.expectsSuffix(rule: Rule<*>): SuffixExpectationRule<Char> {
    return SuffixExpectationRule(ExactCharRule(this), rule)
}

fun Char.requiresPrefix(rule: Rule<*>): RequiresPrefixRule<Char> {
    return RequiresPrefixRule(bodyRule = ExactCharRule(this), prefixRule = rule)
}

infix fun String.or(rule: ExactStringRule): OrRule<CharSequence> {
    return ExactStringRule(this) or rule
}

infix fun String.or(rule: String): OrRule<CharSequence> {
    return ExactStringRule(this) or ExactStringRule(rule)
}

infix fun String.or(rule: Rule<*>): AnyOrRule {
    return ExactStringRule(this) or rule
}

fun String.expectsSuffix(rule: Rule<*>): SuffixExpectationRule<CharSequence> {
    return SuffixExpectationRule(ExactStringRule(this), rule)
}

fun String.requiresPrefix(rule: Rule<*>): RequiresPrefixRule<CharSequence> {
    return RequiresPrefixRule(bodyRule = ExactStringRule(this), prefixRule = rule)
}