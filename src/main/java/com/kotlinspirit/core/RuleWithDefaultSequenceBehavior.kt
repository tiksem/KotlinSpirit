package com.kotlinspirit.core

import com.kotlinspirit.char.ExactCharRule
import com.kotlinspirit.expressive.SequenceRule
import com.kotlinspirit.str.ExactStringRule

abstract class RuleWithDefaultSequenceBehavior<T : Any>(name: String? = null) : Rule<T>(name) {
    /**
     * Returns a rule, that
     * Matches this rule followed by the passed rule
     */
    override operator fun plus(rule: Rule<*>): SequenceRule {
        val c = this
        return SequenceRule(c, rule)
    }

    /**
     * Returns a rule, that
     * Matches this rule followed by the char
     */
    override operator fun plus(char: Char): SequenceRule {
        return SequenceRule(this, ExactCharRule(char))
    }

    /**
     * Returns a rule, that
     * Matches this rule followed by the string
     */
    override operator fun plus(string: String): SequenceRule {
        val c = this
        return SequenceRule(c, ExactStringRule(string))
    }
}