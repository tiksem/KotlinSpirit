package com.kotlinspirit.str.oneof

import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

internal open class TernarySearchTreeCaseInsensitive {
    class Node(
        val char: Char
    ) {
        var eq: Node? = null
        var left: Node? = null
        var right: Node? = null
        var isEndOfWord = false
    }

    val strings: List<CharSequence>
    private val root: Node

    constructor(strings: List<CharSequence>) {
        this.strings = strings.map { it.toString() }.sorted()
        if (strings.isEmpty()) {
            throw IllegalArgumentException("strings should not be empty")
        }

        // Ensure no empty string
        if (strings[0].isEmpty()) {
            throw IllegalStateException("String could not be empty")
        }

        // Create a balanced insertion order
        val balancedList = mutableListOf<CharSequence>()
        balancedOrder(this.strings, 0, this.strings.size - 1, balancedList)

        // Build the root from the first item in balancedList
        root = Node(balancedList[0][0].lowercaseChar())
        insert(root, 0, balancedList[0])

        // Insert the remaining strings in balanced order
        for (i in 1 until balancedList.size) {
            val s = balancedList[i]
            if (s.isEmpty()) {
                throw IllegalStateException("String could not be empty")
            }
            insert(root, 0, s)
        }
    }

    // Helper constructor (unchanged)
    private constructor(root: Node, strings: List<CharSequence>) {
        this.strings = strings
        this.root = root
    }

    /**
     * Recursively pick the middle element to ensure
     * a more balanced insertion order.
     */
    private fun balancedOrder(
        strings: List<CharSequence>,
        start: Int,
        end: Int,
        result: MutableList<CharSequence>
    ) {
        if (start > end) return
        val mid = (start + end) / 2
        result.add(strings[mid])
        balancedOrder(strings, start, mid - 1, result)
        balancedOrder(strings, mid + 1, end, result)
    }

    private fun insert(node: Node, begin: Int, word: CharSequence) {
        val ch = word[begin].lowercaseChar()

        if (node.char == ch) {
            if (begin == word.length - 1) {
                node.isEndOfWord = true
            } else {
                insert(
                    node.eq ?: Node(word[begin + 1].lowercaseChar()).also { node.eq = it },
                    begin + 1,
                    word
                )
            }
        } else if (ch < node.char) {
            insert(
                node.left ?: Node(ch).also { node.left = it },
                begin,
                word
            )
        } else {
            insert(
                node.right ?: Node(ch).also { node.right = it },
                begin,
                word
            )
        }
    }

    fun parse(seek: Int, string: CharSequence): Int {
        return parse(root, seek, string)
    }

    fun reverseParse(seek: Int, string: CharSequence): Int {
        return reverseParse(root, seek, string)
    }

    private fun parse(node: Node, seek: Int, string: CharSequence): Int {
        if (seek >= string.length) {
            return -1
        }
        val ch = string[seek].lowercaseChar()
        val nodeCh = node.char
        return when {
            ch == nodeCh -> {
                if (node.isEndOfWord) {
                    val moreSearch = parse(node.eq ?: return seek + 1, moveSeekToTheNextChar(seek, string), string)
                    if (moreSearch >= 0) moreSearch else seek + 1
                } else {
                    parse(node.eq ?: return -1, moveSeekToTheNextChar(seek, string), string)
                }
            }
            ch < nodeCh -> parse(node.left ?: return -1, seek, string)
            else -> parse(node.right ?: return -1, seek, string)
        }
    }

    private fun reverseParse(node: Node, seek: Int, string: CharSequence): Int {
        if (seek < 0) {
            return -2
        }
        val ch = string[seek].lowercaseChar()
        val nodeCh = node.char
        return when {
            ch == nodeCh -> {
                if (node.isEndOfWord) {
                    val moreSearch = parse(node.eq ?: return seek - 1, moveSeekToThePrevChar(seek, string), string)
                    if (moreSearch >= -1) moreSearch else seek - 1
                } else {
                    parse(node.eq ?: return -2, moveSeekToThePrevChar(seek, string), string)
                }
            }
            ch < nodeCh -> parse(node.left ?: return -2, seek, string)
            else -> parse(node.right ?: return -2, seek, string)
        }
    }

    private fun hasMatch(node: Node, seek: Int, string: CharSequence): Boolean {
        if (seek >= string.length) {
            return false
        }
        val ch = string[seek].lowercaseChar()
        val nodeCh = node.char
        return when {
            ch == nodeCh -> {
                if (node.isEndOfWord) {
                    true
                } else {
                    hasMatch(node.eq ?: return false, moveSeekToTheNextChar(seek, string), string)
                }
            }
            ch < nodeCh -> hasMatch(node.left ?: return false, seek, string)
            else -> hasMatch(node.right ?: return false, seek, string)
        }
    }

    private fun reverseHasMatch(node: Node, seek: Int, string: CharSequence): Boolean {
        if (seek < 0) {
            return false
        }
        val ch = string[seek].lowercaseChar()
        val nodeCh = node.char
        return when {
            ch == nodeCh -> {
                if (node.isEndOfWord) {
                    true
                } else {
                    hasMatch(node.eq ?: return false, moveSeekToThePrevChar(seek, string), string)
                }
            }
            ch < nodeCh -> hasMatch(node.left ?: return false, seek, string)
            else -> hasMatch(node.right ?: return false, seek, string)
        }
    }

    fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return hasMatch(root, seek, string)
    }

    fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return reverseHasMatch(root, seek, string)
    }

    protected open fun moveSeekToTheNextChar(seek: Int, string: CharSequence): Int {
        return seek + 1
    }

    protected open fun moveSeekToThePrevChar(seek: Int, string: CharSequence): Int {
        return seek - 1
    }
}
