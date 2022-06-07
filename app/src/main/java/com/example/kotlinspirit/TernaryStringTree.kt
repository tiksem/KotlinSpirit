package com.example.kotlinspirit

import java.util.concurrent.CopyOnWriteArrayList

internal class TernaryStringTree(strings: List<String>) {
    init {
        strings.forEach {
            insert(it)
        }
    }

    class Node(
        val char: Char,
    ) {
        var eq: Node? = null
        var left: Node? = null
        var right: Node? = null
        internal var isEndOfWord = false
    }

    var root: Node? = null
        private set

    private fun insert(node: Node, word: CharArray, begin: Int) {
        val ch = word[begin]

        if (node.char == ch) {
            if (begin == word.size - 1) {
                node.isEndOfWord = true
            } else {
                insert(node.eq ?: Node(
                    char = word[begin + 1],
                ).also {
                    node.eq = it
                }, word, begin + 1)
            }
        } else if (ch < node.char) {
            insert(node.left ?: Node(
                char = ch,
            ).also {
                node.left = it
            }, word, begin)
        } else {
            insert(node.right ?: Node(
                char = ch,
            ).also {
                node.right = it
            }, word, begin)
        }
    }

    fun insert(word: CharArray, begin: Int) {
        insert(root ?: Node(
            char = word[begin],
        ).also {
            root = it
        }, word, begin)
    }

    fun insert(word: String) {
        insert(word.toCharArray(), 0)
    }

    fun find(node: Node, string: CharSequence, seek: Int): Int? {
        var i = seek
        var n = node
        while (i < string.length) {
            val ch = string[i]
            when {
                ch == n.char -> {
                    i++
                    if (n.isEndOfWord) {
                        val eq = node.eq
                        return if (eq != null) {
                            find(eq, string, i) ?: i
                        } else {
                            i
                        }
                    } else {
                        n = n.eq ?: return null
                    }
                }
                ch < n.char -> {
                    n = n.left ?: return null
                }
                ch > n.char -> {
                    n = n.right ?: return null
                }
            }
        }

        return null
    }

    fun find(string: CharSequence, seek: Int): Int? {
        return find(root ?: return null, string, seek)
    }

    companion object {
        val trees = CopyOnWriteArrayList<TernaryStringTree>()
    }
}
