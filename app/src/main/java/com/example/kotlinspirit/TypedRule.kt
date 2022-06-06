package com.example.kotlinspirit

class TypedRule<T : Any>(
    commands: IntArray
) : Rule<T>(commands) {
    operator fun get(box: Box<T>): TypedRule<T> {
        return TypedRule(addResult(box) + commands)
    }

    operator fun unaryPlus(): ListRule<T> {
        return ListRule(Commands.oneOrMore(commands))
    }

    fun repeat(): ListRule<T> {
        return ListRule(Commands.zeroOrMore(commands))
    }
}