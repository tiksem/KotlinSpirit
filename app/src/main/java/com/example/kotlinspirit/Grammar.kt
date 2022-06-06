package com.example.kotlinspirit

import java.util.concurrent.CopyOnWriteArrayList

abstract class Grammar<T : Any> {
    val commands: IntArray
    private val index: Int

    private val grammarCallCommands
        get() = intArrayOf(Command.GRAMMAR, index)

    init {
        this.commands = createRule().commands
        index = grammars.size
        grammars.add(this as Grammar<Any>)
    }

    abstract fun createRule(): Rule<*>
    abstract fun getResult(): T

    operator fun get(box: Box<T>): TypedRule<T> {
        return TypedRule(
            commands = Rule.addResult(box) + grammarCallCommands
        )
    }

    operator fun unaryPlus(): ListRule<T> {
        return ListRule(Commands.oneOrMore(grammarCallCommands))
    }

    fun repeat(): ListRule<T> {
        return ListRule(Commands.zeroOrMore(grammarCallCommands))
    }

    companion object {
        private val grammars = CopyOnWriteArrayList<Grammar<Any>>()

        fun get(index: Int): Grammar<Any> {
            return grammars[index]
        }
    }
}