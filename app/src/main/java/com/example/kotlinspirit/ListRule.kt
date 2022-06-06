package com.example.kotlinspirit

class ListRule<T>(
    commands: IntArray
) : Rule<List<T>>(commands) {
    operator fun get(box: Box<Any>): AnyRule {
        return AnyRule(addResult(box) + commands)
    }

    operator fun unaryPlus(): ListRule<Any> {
        return ListRule(Commands.oneOrMore(commands))
    }

    fun repeat(): ListRule<Any> {
        return ListRule(Commands.zeroOrMore(commands))
    }

    infix fun or(other: ListRule<T>): ListRule<T> {
        return ListRule(
            Commands.concat(command = Command.OR, a = this.commands, b = other.commands)
        )
    }

    infix fun or(other: ListRule<*>): AnyRule {
        return AnyRule(
            Commands.concat(command = Command.OR, a = this.commands, b = other.commands)
        )
    }
}