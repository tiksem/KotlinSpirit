package com.example.kotlinspirit

operator fun Char.plus(rule: Rule<*>): SequenceRule {
    return ExactCharRule(this) + rule
}

operator fun Char.plus(rule: CharRule): StringRuleWrapper {
    return (ExactCharRule(this) + rule).asStringRule()
}

operator fun Char.plus(rule: OptionalCharRule): StringRuleWrapper {
    return (ExactCharRule(this) + rule).asStringRule()
}