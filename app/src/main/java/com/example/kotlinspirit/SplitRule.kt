package com.example.kotlinspirit

class SplitRule<T : Any>(
    private val rule: Rule<T>,
    private val divider: Rule<*>,
) : Grammar<List<T>>() {
    override var result = ArrayList<T>()
        private set

    override fun defineRule(): Rule<*> {
        return rule {
            result.add(it)
        } + (divider + rule {
            result.add(it)
        }).repeat()
    }

    override fun resetResult() {
        result.clear()
    }

    override fun clone(): SplitRule<T> {
        return SplitRule(rule.clone(), divider.clone())
    }
}