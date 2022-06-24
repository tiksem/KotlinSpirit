package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.str

class SplitRule<T : Any>(
    private val r: Rule<T>,
    private val divider: Rule<*>,
    private val range: IntRange
) : Grammar<List<T>>() {
    override var result = ArrayList<T>()
        private set

    init {
        if (range.first < 0) {
            throw IllegalStateException("negative range.first value")
        }
    }

    override fun defineRule(): Rule<*> {
        return when {
            range.first == 1 && range.last == Int.MAX_VALUE -> {
                r {
                    result.add(it)
                } + (divider + r {
                    result.add(it)
                }).repeat()
            }

            range.first == 0 && range.last == Int.MAX_VALUE -> {
                (r {
                    result.add(it)
                } + (divider + r {
                    result.add(it)
                }).repeat()) or str("")
            }

            range.first == 0 -> {
                (r {
                    result.add(it)
                } + (divider + r {
                    result.add(it)
                }).repeat(IntRange(range.first - 1, range.last - 1))) or str("")
            }

            else -> {
                r {
                    result.add(it)
                } + (divider + r {
                    result.add(it)
                }).repeat(IntRange(range.first - 1, range.last - 1))
            }
        }
    }

    override fun resetResult() {
        result = ArrayList()
    }
}