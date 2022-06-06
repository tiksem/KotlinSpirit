package com.example.kotlinspirit

import it.unimi.dsi.fastutil.ints.IntArrayList

object Commands {
    fun concat(command: Int, a: IntArray, b: IntArray): IntArray {
        val result = IntArrayList()
        if (a[0] == command) {
            if (b[0] == command) {
                result.add(command)
                result.add(a[1] + b[1])
                result.addAll(a.drop(2))
                result.addAll(b.drop(2))
            } else {
                result.add(command)
                result.add(a[1] + 1)
                result.addAll(a.drop(2))
                result.addAll(b.toList())
            }
        } else if (b[0] == command) {
            result.add(command)
            result.add(b[1] + 1)
            result.addAll(a.toList())
            result.addAll(b.drop(2))
        } else {
            result.add(command)
            result.add(2)
            result.addAll(a.toList())
            result.addAll(b.toList())
        }
        return result.toIntArray()
    }

    fun oneOrMore(commands: IntArray): IntArray {
        return intArrayOf(Command.ONE_OR_MORE, commands.size) + commands
    }

    fun zeroOrMore(commands: IntArray): IntArray {
        return intArrayOf(Command.ZERO_OR_MORE, commands.size) + commands
    }
}