package com.example.kotlinspirit

class SplitRule<T : Any>(
    private val r: Rule<T>,
    private val divider: Rule<*>,
) : Grammar<List<T>>() {
    override var result = ArrayList<T>()
        private set

    override fun defineRule(): Rule<*> {
        return r {
            result.add(it)
        } + (divider + r {
            result.add(it)
        }).repeat()
    }

    override fun resetResult() {
        result = ArrayList()
    }

    override fun clone(): SplitRule<T> {
        return SplitRule(r.clone(), r.clone())
    }
}