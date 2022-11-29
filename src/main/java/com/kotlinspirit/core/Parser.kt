package com.kotlinspirit.core

import com.kotlinspirit.debug.RuleDebugTreeNode
import com.kotlinspirit.hook.ParseRange
import com.kotlinspirit.hook.ParseRangeResult

interface Parser<T : Any> {
    fun parseGetResultOrThrow(string: CharSequence): T
    fun parseOrThrow(string: CharSequence): Int
    fun tryParse(string: CharSequence): Int?
    fun parseWithResult(string: CharSequence): ParseResult<T>
    fun parse(string: CharSequence): ParseSeekResult
    fun matches(string: CharSequence): Boolean
    fun matchOrThrow(string: CharSequence)
    fun matchesAtBeginning(string: CharSequence): Boolean

    fun indexOf(string: CharSequence): Int?
    fun findFirstResult(string: CharSequence): ParseRangeResult<T>?
    fun findFirst(string: CharSequence): T?
    fun findFirstRange(string: CharSequence): ParseRange?
    fun findAll(string: CharSequence): List<T>
    fun findAllResults(string: CharSequence): List<ParseRangeResult<T>>

    fun replaceFirst(source: CharSequence, replacement: CharSequence): CharSequence
    fun replaceAll(source: CharSequence, replacement: CharSequence): CharSequence
    fun replaceFirst(source: CharSequence, replacementProvider: (T) -> Any): CharSequence
    fun replaceAll(source: CharSequence, replacementProvider: (T) -> Any): CharSequence

    fun replaceFirstOrNull(source: CharSequence, replacement: CharSequence): CharSequence?
    fun replaceFirstOrNull(source: CharSequence, replacementProvider: (T) -> CharSequence): CharSequence?

    fun getDebugTree(): RuleDebugTreeNode? {
        return null
    }

    fun getDebugHistory(): List<RuleDebugTreeNode> {
        return emptyList()
    }
}