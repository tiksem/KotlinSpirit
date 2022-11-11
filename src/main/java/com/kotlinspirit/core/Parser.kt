package com.kotlinspirit.core

import com.kotlinspirit.debug.RuleDebugTreeNode

interface Parser<T : Any> {
    fun parseGetResultOrThrow(string: CharSequence): T
    fun parseOrThrow(string: CharSequence): Int
    fun tryParse(string: CharSequence): Int?
    fun parseWithResult(string: CharSequence): ParseResult<T>
    fun parse(string: CharSequence): ParseSeekResult
    fun matches(string: CharSequence): Boolean
    fun matchOrThrow(string: CharSequence)
    fun matchesAtBeginning(string: CharSequence): Boolean
    fun replaceFirst(source: CharSequence, replacement: CharSequence): CharSequence
    fun replaceAll(source: CharSequence, replacement: CharSequence): CharSequence

    fun getDebugTree(): RuleDebugTreeNode? {
        return null
    }
}