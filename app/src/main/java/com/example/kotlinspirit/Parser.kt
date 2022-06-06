package com.example.kotlinspirit

import kotlin.concurrent.withLock

class Parser internal constructor(
    private var results: List<Box<Any>>,
    isGrammarParser: Boolean = false
) {
    private var string: CharSequence = ""
    private var seek = 0
    private var commandIndex = 0
    private var commands = intArrayOf()
    private lateinit var grammarParsers: ObjectPool<Parser>
    private var threadId = Thread.currentThread().id
    private val arrayListPool = ObjectPool {
        ArrayList<Any>()
    }

    constructor() : this(
        emptyList()
    ) {
        this.results = Rule.getResults()
    }

    init {
        if (!isGrammarParser) {
            grammarParsers = ObjectPool {
                Parser(results, true).also {
                    it.grammarParsers = grammarParsers
                }
            }
        }
    }

    private fun parse(commands: IntArray, string: CharSequence, requireResult: Boolean): Any? {
        if (threadId != Thread.currentThread().id) {
            throw ConcurrentModificationException(
                "Parser should be created and used on the same thread"
            )
        }

        seek = 0
        commandIndex = 0
        this.commands = commands
        this.string = string

        return handleCommand(commands[0], requireResult)
    }

    fun match(commands: IntArray, string: CharSequence): Boolean {
        return parse(commands, string, false) != null
    }

    fun parse(commands: IntArray, string: CharSequence): Any? {
        return parse(commands, string, true)
    }

    fun match(rule: Rule<*>, string: CharSequence): Boolean {
        return match(rule.commands, string)
    }

    fun <T : Any> parse(rule: Rule<T>, string: CharSequence): T? {
        return parse(rule.commands, string, true) as? T
    }

    fun match(grammar: Grammar<*>, string: CharSequence): Boolean {
        return match(grammar.commands, string)
    }

    fun <T : Any> parse(grammar: Grammar<T>, string: CharSequence): T? {
        return if (match(grammar.commands, string)) {
            grammar.getResult()
        } else {
            null
        }
    }

    private fun handleCommand(command: Int, requireResult: Boolean): Any? {
        return when (command) {
            Command.MATCH_CHAR -> {
                return handleChar()
            }
            Command.ANY_CHAR -> {
                handleAnyChar()
            }
            Command.ANY_INT -> {
                handleInt()
            }
            Command.ANY_UNSIGNED_INT -> {
                handleUInt()
            }
            Command.OR -> {
                handleOrCommand(requireResult)
            }
            Command.SEQUENCE -> {
                handleSequenceCommand(requireResult)
            }
            Command.ONE_OR_MORE -> {
                handleOneOrMore(requireResult)
            }
            Command.ZERO_OR_MORE -> {
                handleZeroOrMore(requireResult)
            }
            Command.REPEAT -> {
                handleRepeat(requireResult)
            }
            Command.RESULT -> {
                handleResultCallbackCommand()
            }
            Command.GRAMMAR -> {
                handleGrammarCommand(requireResult)
            }
            else -> null
        }
    }

    private fun handleGrammarCommand(requireResult: Boolean): Any? {
        val parser = grammarParsers.take()
        parser.seek = seek
        val grammarIndex = commands[++commandIndex]
        val grammar = Grammar.get(grammarIndex)
        return if (parser.match(grammar.commands, string)) {
            if (requireResult) grammar.getResult() else true
        } else {
            null
        }.also {
            grammarParsers.putBack(parser)
        }
    }

    private fun handleResultCallbackCommand(): Any? {
        val callbackId = commands[++commandIndex]
        commandIndex++
        val command = commands[commandIndex]
        val result = handleCommand(command, requireResult = true)
        if (result != null) {
            val box = results[callbackId]
            box.data = result
        }

        return result
    }

    private fun handleSequenceCommand(requireResult: Boolean): CharSequence? {
        val beginSeek = seek
        val numberOfCommands = commands[++commandIndex]
        val lastCommandIndex = numberOfCommands + commandIndex
        commandIndex++
        while (commandIndex <= lastCommandIndex) {
            val command = commands[commandIndex]
            val value = handleCommand(command, false)
            if (value == null) {
                seek = beginSeek
                return null
            }
            commandIndex++
        }

        return if (requireResult) {
            string.subSequence(beginSeek, seek)
        } else {
            ""
        }
    }

    private fun handleOneOrMore(requireResult: Boolean): List<Any>? {
        val beginSeek = seek
        val command = commands[++commandIndex]
        val beginCommandIndex = commandIndex
        if (requireResult) {
            val result = arrayListPool.take()
            while (true) {
                val item = handleCommand(command, requireResult)
                if (item == null) {
                    break
                } else {
                    result.add(item)
                }
                commandIndex = beginCommandIndex
            }

            return if (result.size < 0) {
                null
            } else {
                result
            }
        } else {
            var hasResults = false
            while (handleCommand(command, requireResult) != null) {
                commandIndex = beginCommandIndex
                hasResults = true
            }

            return if (hasResults) {
                emptyList()
            } else {
                seek = beginSeek
                null
            }
        }
    }

    private fun handleRepeat(requireResult: Boolean): List<Any>? {
        val beginSeek = seek
        val min = commands[++commandIndex]
        val max = commands[++commandIndex]
        val command = commands[++commandIndex]
        val beginCommandIndex = commandIndex

        if (requireResult) {
            val result = arrayListPool.take()
            while (true) {
                val item = handleCommand(command, requireResult)
                if (item == null) {
                    break
                } else {
                    result.add(item)
                }
                commandIndex = beginCommandIndex
            }

            return if (result.size in min..max) {
                result
            } else {
                seek = beginSeek
                null
            }
        } else {
            var numberOfResults = 0
            while (handleCommand(command, requireResult) != null) {
                numberOfResults++
                commandIndex = beginCommandIndex
            }

            return if (numberOfResults in min..max) {
                emptyList()
            } else {
                seek = beginSeek
                null
            }
        }
    }

    private fun handleZeroOrMore(requireResult: Boolean): List<Any> {
        val command = commands[++commandIndex]
        val beginCommandIndex = commandIndex
        if (requireResult) {
            val result = arrayListPool.take()
            while (true) {
                val item = handleCommand(command, requireResult)
                if (item == null) {
                    break
                } else {
                    result.add(item)
                }
                commandIndex = beginCommandIndex
            }

            return result
        } else {
            while (handleCommand(command, requireResult) != null) {
                commandIndex = beginCommandIndex
            }
            return emptyList()
        }
    }

    private fun handleOrCommand(requireResult: Boolean): Any? {
        val numberOfCommands = commands[++commandIndex]
        val orLastCommandIndex = numberOfCommands + commandIndex
        commandIndex++
        while (commandIndex <= orLastCommandIndex) {
            val beginSeek = seek
            val command = commands[commandIndex]
            val result = handleCommand(command, requireResult)
            if (result != null) {
                return result
            }
            seek = beginSeek
            commandIndex++
        }

        return null
    }

    private fun handleAnyChar(): Char? {
        val length = string.length
        if (seek >= length) {
            return null
        }

        return string[seek++]
    }

    private fun handleChar(): Char? {
        val length = string.length
        if (seek >= length) {
            return null
        }

        val predicateIndex = commands[++commandIndex]
        val predicate = CharMatchData.predicates[predicateIndex]
        val char = string[seek]
        return if (predicate(char)) {
            seek++
            char
        } else {
            null
        }
    }

    private fun handleInt(): Int? {
        val length = string.length
        if (seek >= length) {
            return null
        }

        val beginSeek = seek
        var sign = 1
        var result = 0
        var successFlag = false
        do {
            val char = string[seek++]
            when {
                char == '-' && !successFlag -> {
                    sign = -1
                }
                !successFlag && char == '0' -> {
                    return if (seek >= length || string[seek] !in '0'..'9') {
                        0
                    } else {
                        seek = beginSeek
                        null
                    }
                }
                char in '0'..'9' -> {
                    successFlag = true
                    result += 10
                    result += char - '0'
                    // check int bounds
                    if (result < 0) {
                        seek = beginSeek
                        return null
                    }
                }
                successFlag -> {
                    return result * sign
                }
                else -> {
                    seek = beginSeek
                    return null
                }
            }
        } while (seek < length)

        return result * sign
    }

    private fun handleUInt(): Int? {
        val length = string.length
        if (seek >= length) {
            return null
        }

        val beginSeek = seek
        var result = 0
        var successFlag = false
        do {
            val char = string[seek++]
            when {
                !successFlag && char == '0' -> {
                    return if (seek >= length || string[seek] !in '0'..'9') {
                        0
                    } else {
                        seek = beginSeek
                        null
                    }
                }
                char in '0'..'9' -> {
                    successFlag = true
                    result += 10
                    result += char - '0'
                    // check int bounds
                    if (result < 0) {
                        seek = beginSeek
                        return null
                    }
                }
                successFlag -> {
                    return result
                }
                else -> {
                    seek = beginSeek
                    return null
                }
            }
        } while (seek < length)

        return result
    }


}