package com.kotlinspirit.str.oneof

internal open class TernarySearchTree {
    class Node(
        val char: Char
    ) {
        var eq: Node? =  null
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

        // Make sure there’s no empty string
        if (strings[0].isEmpty()) {
            throw IllegalStateException("String could not be empty")
        }

        // Build a list of strings in “balanced insertion” order:
        val balancedList = mutableListOf<CharSequence>()
        balancedOrder(this.strings, 0, strings.size - 1, balancedList)

        // The first in balancedList becomes our root node
        root = Node(balancedList[0][0])
        insert(root, 0, balancedList[0])

        // Insert the rest in balanced order, reusing insert
        for (i in 1 until balancedList.size) {
            val s = balancedList[i]
            if (s.isEmpty()) {
                throw IllegalStateException("String could not be empty")
            }
            insert(root, 0, s)
        }
    }

    // Helper constructor used elsewhere
    private constructor(root: Node, strings: List<String>) {
        this.strings = strings
        this.root = root
    }

    /**
     * Recursively pick middle element to ensure
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
        val ch = word[begin]
        if (node.char == ch) {
            if (begin == word.length - 1) {
                node.isEndOfWord = true
            } else {
                insert(node.eq ?: Node(
                    char = word[begin + 1],
                ).also {
                    node.eq = it
                }, begin + 1, word)
            }
        } else if (ch < node.char) {
            insert(node.left ?: Node(
                char = ch,
            ).also {
                node.left = it
            }, begin, word)
        } else {
            insert(node.right ?: Node(
                char = ch,
            ).also {
                node.right = it
            }, begin, word)
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

        val ch = string[seek]
        val nodeCh = node.char
        when {
            ch == nodeCh -> {
                if (node.isEndOfWord) {
                    val moreSearch = parse(node.eq ?: return seek + 1, moveSeekToTheNextChar(seek, string), string)
                    return if (moreSearch >= 0) {
                        moreSearch
                    } else {
                        seek + 1
                    }
                } else {
                    return parse(node.eq ?: return -1, moveSeekToTheNextChar(seek, string), string)
                }
            }
            ch < nodeCh -> {
                return parse(node.left ?: return -1, seek, string)
            }
            else -> {
                return parse(node.right ?: return -1, seek, string)
            }
        }
    }

    private fun reverseParse(node: Node, seek: Int, string: CharSequence): Int {
        if (seek < 0) {
            return -2
        }

        val ch = string[seek]
        val nodeCh = node.char
        when {
            ch == nodeCh -> {
                if (node.isEndOfWord) {
                    val moreSearch = parse(node.eq ?: return seek - 1, moveSeekToThePrevChar(seek, string), string)
                    return if (moreSearch >= -1) {
                        moreSearch
                    } else {
                        seek - 1
                    }
                } else {
                    return parse(node.eq ?: return -2, moveSeekToThePrevChar(seek, string), string)
                }
            }
            ch < nodeCh -> {
                return parse(node.left ?: return -2, seek, string)
            }
            else -> {
                return parse(node.right ?: return -2, seek, string)
            }
        }
    }

    private fun hasMatch(node: Node, seek: Int, string: CharSequence): Boolean {
        if (seek >= string.length) {
            return false
        }

        val ch = string[seek]
        val nodeCh = node.char
        return when {
            ch == nodeCh -> {
                if (node.isEndOfWord) {
                    true
                } else {
                    hasMatch(node.eq ?: return false, moveSeekToTheNextChar(seek, string), string)
                }
            }
            ch < nodeCh -> {
                hasMatch(node.left ?: return false, seek, string)
            }
            else -> {
                hasMatch(node.right ?: return false, seek, string)
            }
        }
    }

    private fun reverseHasMatch(node: Node, seek: Int, string: CharSequence): Boolean {
        if (seek < 0) {
            return false
        }

        val ch = string[seek]
        val nodeCh = node.char
        return when {
            ch == nodeCh -> {
                if (node.isEndOfWord) {
                    true
                } else {
                    hasMatch(node.eq ?: return false, moveSeekToThePrevChar(seek, string), string)
                }
            }
            ch < nodeCh -> {
                hasMatch(node.left ?: return false, seek, string)
            }
            else -> {
                hasMatch(node.right ?: return false, seek, string)
            }
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
