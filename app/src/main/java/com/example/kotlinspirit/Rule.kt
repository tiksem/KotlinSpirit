package com.example.kotlinspirit

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

open class Rule<T : Any>(
    val commands: IntArray
) {
    infix fun or(other: Rule<*>): AnyRule {
        return AnyRule(
            Commands.concat(command = Command.OR, a = this.commands, b = other.commands)
        )
    }

    override fun toString(): String {
        val result = StringBuilder()
        var i = 0
        while (true) {
            when (commands[i]) {
                Command.OR -> {
                    result.append("OR ")
                    result.append(commands[++i])
                }
                Command.SEQUENCE -> {
                    result.append("SEQUENCE ")
                    result.append(commands[++i])
                }
                Command.ANY_INT -> {
                    result.append("ANY_INT")
                }
            }
            i++
            if (i < commands.size) {
                result.append(" ")
            } else {
                break
            }
        }

        return result.toString()
    }

    companion object {
        private val resultsLock = ReentrantLock()
        private val results = Long2ObjectOpenHashMap<ArrayList<Box<Any>>>()

        internal fun <T : Any> addResult(box: Box<T>): IntArray {
            val id: Int
            resultsLock.withLock {
                id = results.size
                if (results.isEmpty()) {
                    results[Thread.currentThread().id] = arrayListOf(box as Box<Any>)
                } else {
                    results.values.forEach {
                        it.add(box as Box<Any>)
                    }
                }
            }
            return intArrayOf(Command.RESULT, id)
        }

        internal fun getResults() = resultsLock.withLock {
            val threadId = Thread.currentThread().id
            results.getOrPut(threadId) {
                results.values.first().map {
                    it.copy()
                }.asArrayList()
            }
        }
    }
}