package com.example.kotlinspirit

open class CharRule(
    commands: IntArray
) : Rule<Char>(commands) {
    companion object {
        internal val ANY_CHAR = AnyCharRule()
    }
}

class AnyCharRule : CharRule(commands = intArrayOf(Command.ANY_CHAR)) {
    operator fun invoke(predicate: (Char) -> Boolean): MatchCharRuleCustomPredicate {
        CharMatchData.predicates.add(predicate)
        val index = CharMatchData.predicates.indexOf(predicate)
        return MatchCharRuleCustomPredicate(
            commands = intArrayOf(Command.MATCH_CHAR, index),
            predicateIndex = index
        )
    }

    infix fun or(other: CharRule): AnyCharRule {
        return this
    }

    private fun repeat(range: IntRange): StringRule {
        return StringRule(
            commands = intArrayOf(
                Command.ANY_CHAR_STR, range.first, range.last
            )
        )
    }

    operator fun unaryPlus(): StringRule {
        return repeat(1..Int.MAX_VALUE)
    }

    fun repeat(): StringRule {
        return repeat(0..Int.MAX_VALUE)
    }

    infix fun or(other: AnyCharRule): AnyCharRule {
        return other
    }
}

class MatchCharRule(
    commands: IntArray,
    private val data: CharMatchData,
) : CharRule(commands) {
    private fun repeat(range: IntRange): StringRule {
        return MatchStringRule(
            commands = intArrayOf(
                Command.MATCH_CHAR_STR, commands[1], range.first, range.last
            ),
            data
        )
    }

    operator fun unaryPlus(): StringRule {
        return repeat(1..Int.MAX_VALUE)
    }

    fun repeat(): StringRule {
        return repeat(0..Int.MAX_VALUE)
    }

    infix fun or(other: AnyCharRule): AnyCharRule {
        return other
    }

    infix fun or(other: MatchCharRule): MatchCharRule {
        val otherData = other.data
        val data = data.merge(other.data)
        return MatchCharRule(
            intArrayOf(Command.MATCH_CHAR, data.predicateIndex),
            data
        )
    }

    infix fun or(other: MatchCharRuleCustomPredicate): MatchCharRuleCustomPredicate {
        val predicate = data.predicate
        val otherPredicate = CharMatchData.predicates[other.predicateIndex]
        val resultPredicate: (Char) -> Boolean = {
            predicate(it) || otherPredicate(it)
        }

        CharMatchData.predicates.add(resultPredicate)
        val index = CharMatchData.predicates.indexOf(resultPredicate)
        return MatchCharRuleCustomPredicate(
            commands = intArrayOf(Command.MATCH_CHAR, index),
            predicateIndex = index
        )
    }
}

class MatchCharRuleCustomPredicate(
    commands: IntArray,
    val predicateIndex: Int
) : CharRule(commands) {
    infix fun or(other: MatchCharRuleCustomPredicate): MatchCharRuleCustomPredicate {
        val predicate = CharMatchData.predicates[predicateIndex]
        val otherPredicate = CharMatchData.predicates[other.predicateIndex]
        val resultPredicate: (Char) -> Boolean = {
            predicate(it) || otherPredicate(it)
        }

        CharMatchData.predicates.add(resultPredicate)
        val index = CharMatchData.predicates.indexOf(resultPredicate)
        return MatchCharRuleCustomPredicate(
            commands = intArrayOf(Command.MATCH_CHAR, index),
            predicateIndex = index
        )
    }
}

open class StringRule(
    commands: IntArray,
) : Rule<CharSequence>(commands) {
    private fun repeat(range: IntRange): ListRule<CharSequence> {
        return ListRule(
            commands = intArrayOf(
                Command.REPEAT, range.first, range.last
            ) + commands
        )
    }

    operator fun unaryPlus(): ListRule<CharSequence> {
        return ListRule(
            commands = intArrayOf(
                Command.ONE_OR_MORE
            ) + commands
        )
    }

    fun repeat(): ListRule<CharSequence> {
        return ListRule(
            commands = intArrayOf(
                Command.ZERO_OR_MORE
            ) + commands
        )
    }
}

class MatchStringRule(
    commands: IntArray,
    private val data: CharMatchData
) : StringRule(commands) {

}