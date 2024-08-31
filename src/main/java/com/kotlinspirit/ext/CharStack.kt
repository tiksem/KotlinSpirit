package com.kotlinspirit.ext

internal class CharStack(initialSize: Int = 16) {
    private var stack: CharArray = CharArray(initialSize)
    private var size = 0

    // Function to push a character onto the stack
    fun push(c: Char) {
        if (size >= stack.size) {
            resize() // Resize the stack if it's full
        }
        stack[size] = c
        size++
    }

    // Function to pop a character from the stack
    fun pop(): Char? {
        if (size == 0) return null
        size--
        return stack[size]
    }

    // Function to peek at the top character of the stack without popping it
    fun peek(): Char? {
        return if (size == 0) null else stack[size - 1]
    }

    // Function to check if the stack is empty
    fun isEmpty(): Boolean {
        return size == 0
    }

    // Internal function to resize the stack when it gets full
    private fun resize() {
        val newStack = CharArray(stack.size * 2)
        for (i in stack.indices) {
            newStack[i] = stack[i]
        }
        stack = newStack
    }
}
