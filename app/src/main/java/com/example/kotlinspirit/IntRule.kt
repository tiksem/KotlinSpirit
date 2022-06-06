package com.example.kotlinspirit

class IntRule(
    commands: IntArray
) : Rule<Int>(commands) {
    infix fun or(other: AnyRule): AnyRule {
        return AnyRule(
            Commands.concat(command = Command.OR, a = this.commands, b = other.commands)
        )
    }

    infix fun or(other: IntRule): IntRule {
        return IntRule(
            Commands.concat(command = Command.OR, a = this.commands, b = other.commands)
        )
    }

    operator fun get(box: Box<Int>): IntRule {
        return IntRule(
            commands = addResult(box as Box<Any>) + commands
        )
    }

    operator fun unaryPlus(): ListRule<Int> {
        return ListRule(Commands.oneOrMore(commands))
    }

    fun repeat(): ListRule<Int> {
        return ListRule(Commands.zeroOrMore(commands))
    }
}