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
        time = System.currentTimeMillis()
        strs.forEach {
            (1..it.length).forEach { end ->
                if (set.contains(it.subSequence(0, end))) {
                    return@forEach
                }
            }
        }
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
        time = System.currentTimeMillis()
        repeat(1000) {
            strs.forEach {
                strings.forEach { s ->
                    it.startsWith(s)
                }
            }
        }
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
        time = System.currentTimeMillis()
        repeat(10000) {
            strs.forEach {
                strings.forEach { s ->
                    it.startsWith(s)
                }
            }
        }
    }
}