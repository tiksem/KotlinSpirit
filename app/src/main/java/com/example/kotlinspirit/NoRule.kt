package com.example.kotlinspirit

private const val NO_RULE_FAILED = "no rule failed"

class NoRule<T>(
    private val rule: Rule<T>
) : StringRule() {
    override fun parse(state: ParseState, requireResult: Boolean) {
        rule.parse(state)
        if (!state.hasError) {
            state.errorReason = NO_RULE_FAILED
        }
    }
}