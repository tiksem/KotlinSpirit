package com.example.kotlinspirit

import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException

class TernaryStringTree(strings: List<String>) {
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

    fun getIterator(): ParseIterator<CharSequence> {
        val root = this.root
        if (root == null) {
            return object : BaseParseIterator<CharSequence>() {
                override fun getResult(): CharSequence {
                    return ""
                }

                override fun next(): Int {
                    return StepCode.COMPLETE
                }
            }
        } else {
            return Iterator(root)
        }
    }

    private class Iterator(
        private val originalNode: Node
    ) : BaseStringIterator() {
        private var node = originalNode
        private var intermediateResultSeek = -1

        private fun checkIntermediateSeek(errorCode: Int): Int {
            return if (intermediateResultSeek >= 0) {
                seek = intermediateResultSeek
                StepCode.COMPLETE
            } else {
                errorCode
            }
        }

        override fun resetSeek(seek: Int) {
            super.resetSeek(seek)
            intermediateResultSeek = -1
            node = originalNode
        }

        override fun prev() {
            throw UnsupportedOperationException("TernaryStringTree iterator doesn't support prev")
        }

        override fun next(): Int {
            if (isEof()) {
                return checkIntermediateSeek(StepCode.EOF)
            }

            val ch = getChar()
            when {
                node.char == ch -> {
                    seek++
                    if (node.isEndOfWord) {
                        if (node.eq != null) {
                            intermediateResultSeek = seek
                        } else {
                            return StepCode.COMPLETE
                        }
                    } else {
                        val eq = node.eq
                        if (eq != null) {
                            node = eq
                        }
                    }

                    return StepCode.HAS_NEXT
                }
                ch < node.char -> {
                    val left = node.left
                    return if (left == null) {
                        seek++
                        checkIntermediateSeek(StepCode.ONE_OF_STRING_NOT_FOUND)
                    } else {
                        node = left
                        next()
                    }
                }
                else -> {
                    val right = node.right
                    return if (right == null) {
                        seek++
                        checkIntermediateSeek(StepCode.ONE_OF_STRING_NOT_FOUND)
                    } else {
                        node = right
                        next()
                    }
                }
            }
        }
    }
}
