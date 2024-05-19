package com.kotlinspirit.str.oneof

import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

internal open class TernarySearchTreeCaseInsensitive {
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
        this.strings = strings
        if (strings.isEmpty()) {
            throw IllegalArgumentException("strings should not be empty")
        }

        val firstString = strings[0]
        if (firstString.isEmpty()) {
            throw IllegalStateException("String could not be empty")
        }

        root = Node(
            char = firstString[0]
        )

        strings.forEach {
            if (it.isNotEmpty()) {
                insert(root, 0, it)
            } else {
                throw IllegalStateException("String could not be empty")
            }
        }
    }

    private constructor(root: Node, strings: List<CharSequence>) {
        this.strings = strings
        this.root = root
    }

    private fun insert(node: Node, begin: Int, word: CharSequence) {
        val ch = word[begin].lowercaseChar()

        if (node.char == ch) {
            if (begin == word.length - 1) {
                node.isEndOfWord = true
            } else {
                insert(node.eq ?: Node(
                    char = word[begin + 1].lowercaseChar(),
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

        val ch = string[seek].lowercaseChar()
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

        val ch = string[seek].lowercaseChar()
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

        val ch = string[seek].lowercaseChar()
        val nodeCh = node.char
        when {
            ch == nodeCh -> {
                if (node.isEndOfWord) {
                    return true
                } else {
                    return hasMatch(node.eq ?: return false, moveSeekToTheNextChar(seek, string), string)
                }
            }
            ch < nodeCh -> {
                return hasMatch(node.left ?: return false, seek, string)
            }
            else -> {
                return hasMatch(node.right ?: return false, seek, string)
            }
        }
    }

    private fun reverseHasMatch(node: Node, seek: Int, string: CharSequence): Boolean {
        if (seek < 0) {
            return false
        }

        val ch = string[seek].lowercaseChar()
        val nodeCh = node.char
        when {
            ch == nodeCh -> {
                if (node.isEndOfWord) {
                    return true
                } else {
                    return hasMatch(node.eq ?: return false, moveSeekToThePrevChar(seek, string), string)
                }
            }
            ch < nodeCh -> {
                return hasMatch(node.left ?: return false, seek, string)
            }
            else -> {
                return hasMatch(node.right ?: return false, seek, string)
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
        return seek+1
    }

    protected open fun moveSeekToThePrevChar(seek: Int, string: CharSequence): Int {
        return seek-1
    }
}