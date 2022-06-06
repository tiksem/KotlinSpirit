package com.example.kotlinspirit

class AnyRule(
    commands: IntArray
) : Rule<Any>(commands) {
    operator fun get(box: Box<Any>): AnyRule {
        return AnyRule(addResult(box) + commands)
    }

    operator fun unaryPlus(): ListRule<Any> {
        return ListRule(Commands.oneOrMore(commands))
    }

    fun repeat(): ListRule<Any> {
        return ListRule(Commands.zeroOrMore(commands))
    }
}