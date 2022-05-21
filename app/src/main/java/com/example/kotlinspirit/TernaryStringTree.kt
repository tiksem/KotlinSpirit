package com.example.kotlinspirit

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

    fun search(node: Node, word: CharArray, begin: Int): Node? {
        val ch = word[begin]
        if (node.char == ch) {
            if (begin == word.size - 1) {
                return if (node.isEndOfWord) {
                    node
                } else {
                    null
                }
            } else {
                return search(node.eq ?: return null, word, begin + 1)
            }
        } else if (ch < node.char) {
            return search(node.left ?: return null, word, begin)
        } else {
            return search(node.right ?: return null, word, begin)
        }
    }

    fun search(word: CharArray, begin: Int): Node? {
        return search(root ?: return null, word, begin)
    }

    fun search(state: ParseState): Node? {
        return search(root ?: return null, state)
    }

    fun search(node: Node, state: ParseState): Node? {
        if (state.seek >= state.array.size) {
            return null
        }

        val ch = state.getChar()
        when {
            node.char == ch -> {
                state.seek++
                val nodeSeek = state.seek
                val eq = node.eq?.let { search(it, state) }?.takeIf { it.isEndOfWord }
                return when {
                    eq != null -> {
                        eq
                    }
                    node.isEndOfWord -> {
                        state.seek = nodeSeek
                        node
                    }
                    else -> {
                        null
                    }
                }
            }
            ch < node.char -> {
                return search(node.left ?: return null, state)
            }
            else -> {
                return search(node.right ?: return null, state)
            }
        }
    }
}
