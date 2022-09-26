package com.kotlinspirit

import com.kotlinspirit.str.oneof.TernarySearchTree
import org.junit.Test
import kotlin.random.Random
import kotlin.random.nextInt

class TernarySearchTreePerformanceTest {
    @Test
    fun large() {
        val strings = (0..100000).map {
            val length = Random.nextInt(1..100)
            val res = StringBuilder()
            repeat(length) {
                res.append(Random.nextInt('a'.code..'z'.code).toChar())
            }
            res.toString()
        }
        val tree = TernarySearchTree(strings)
        val set = HashSet<CharSequence>(strings)

        val strs = strings.shuffled()
        var time = System.currentTimeMillis()
        strs.forEach {
            tree.parse(0, it)
        }
        println("large")
        println("tree=" + (System.currentTimeMillis() - time))
        time = System.currentTimeMillis()
        strs.forEach {
            (1..it.length).forEach { end ->
                if (set.contains(it.subSequence(0, end))) {
                    return@forEach
                }
            }
        }
        println("set=" + (System.currentTimeMillis() - time))
    }

    @Test
    fun small() {
        val strings = (0..10).map {
            val length = Random.nextInt(1..100)
            val res = StringBuilder()
            repeat(length) {
                res.append(Random.nextInt('a'.code..'z'.code).toChar())
            }
            res.toString()
        }
        val tree = TernarySearchTree(strings)
        val set = HashSet<CharSequence>(strings)

        val strs = strings.shuffled()
        var time = System.currentTimeMillis()
        repeat(1000) {
            strs.forEach {
                tree.parse(0, it)
            }
        }
        println("small")
        println("tree=" + (System.currentTimeMillis() - time))
        time = System.currentTimeMillis()
        repeat(1000) {
            strs.forEach {
                (1..it.length).forEach { end ->
                    if (set.contains(it.subSequence(0, end))) {
                        return@forEach
                    }
                }
            }
        }
        println("set=" + (System.currentTimeMillis() - time))
        time = System.currentTimeMillis()
        repeat(1000) {
            strs.forEach {
                strings.forEach { s ->
                    it.startsWith(s)
                }
            }
        }
        println("array=" + (System.currentTimeMillis() - time))
    }

    @Test
    fun only2() {
        val strings = (0..1).map {
            val length = Random.nextInt(1..100)
            val res = StringBuilder()
            repeat(length) {
                res.append(Random.nextInt('a'.code..'z'.code).toChar())
            }
            res.toString()
        }
        val tree = TernarySearchTree(strings)
        val set = HashSet<CharSequence>(strings)

        val strs = strings.shuffled()
        var time = System.currentTimeMillis()
        repeat(1000) {
            strs.forEach {
                tree.parse(0, it)
            }
        }
        println("only2")
        println("tree=" + (System.currentTimeMillis() - time))
        time = System.currentTimeMillis()
        repeat(10000) {
            strs.forEach {
                (1..it.length).forEach { end ->
                    if (set.contains(it.subSequence(0, end))) {
                        return@forEach
                    }
                }
            }
        }
        println("set=" + (System.currentTimeMillis() - time))
        time = System.currentTimeMillis()
        repeat(10000) {
            strs.forEach {
                strings.forEach { s ->
                    it.startsWith(s)
                }
            }
        }
        println("array=" + (System.currentTimeMillis() - time))
    }
}