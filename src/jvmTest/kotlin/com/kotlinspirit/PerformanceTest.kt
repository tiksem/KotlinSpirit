package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.digit
import com.kotlinspirit.core.Rules.double
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.core.Rules.oneOf
import com.kotlinspirit.core.Rules.uint
import org.junit.Test
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class PerformanceTest {
    @Test
    fun test1() {
        val pattern = Regex("^(?:Ivan|Vasil|Vivaldi|[A-Z]+)+@[0-9]+$")
        val parser = (
                (oneOf("Ivan", "Vasil", "Vivaldi") or char('A'..'Z'))
                    .repeat().asString() + '@' + digit.repeat()
                )
            .compile()

        val input = StringBuilder()
        val arr = arrayOf("Ivan", "Vasil", "Vivaldi")
        repeat(500) {
            if (Random.nextBoolean()) {
                input.append(Random.nextInt('A'.code, 'Z'.code + 1).toChar())
            } else {
                input.append(arr.random())
            }
            input.append(arr.random())
        }
        input.append('@')
        repeat(500) {
            input.append(Random.nextInt(0, 10).digitToChar())
        }

        var time = System.currentTimeMillis()
        repeat(1000) {
            parser.parseOrThrow(input)
        }
        System.out.println(System.currentTimeMillis() - time)
        time = System.currentTimeMillis()
        repeat(1000) {
            pattern.matches(input)
        }
        System.out.println(System.currentTimeMillis() - time)
    }

    @Test
    fun test2() {
        // Setup
        val regexPattern = Regex(
            """((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"""
        )
        val kotlinSpiritRule = (uint(0.toUInt()..255.toUInt()) - (char('0') + +digit)).split('.', 4).compile()

        // Generate 1 million random IPs
        println("Generating 1,000,000 random IP addresses...")
        val ips = mutableListOf<String>()
        repeat(1_000_000) {
            when (Random.nextInt(10)) {
                in 0..6 -> {
                    // 70% valid IPs
                    val octets = List(4) { Random.nextInt(0, 256) }
                    ips.add(octets.joinToString("."))
                }
                7 -> {
                    // 10% invalid - out of range
                    val octets = List(4) { Random.nextInt(0, 500) }
                    ips.add(octets.joinToString("."))
                }
                8 -> {
                    // 10% invalid - wrong number of octets
                    val octets = List(Random.nextInt(2, 6)) { Random.nextInt(0, 256) }
                    ips.add(octets.joinToString("."))
                }
                9 -> {
                    // 10% invalid - non-numeric
                    ips.add("not.an.ip.address")
                }
            }
        }
        println("Generation complete.\n")

        // Warm-up
        println("Warming up...")
        repeat(3) {
            ips.forEach { regexPattern.matches(it) }
            ips.forEach { kotlinSpiritRule.matches(it) }
        }
        println("Warm-up complete.\n")

        // Benchmark Regex
        var regexCount = 0
        val regexTime = measureTimeMillis {
            ips.forEach { if (regexPattern.matches(it)) regexCount++ }
        }

        // Benchmark KotlinSpirit
        var ksCount = 0
        val ksTime = measureTimeMillis {
            ips.forEach { if (kotlinSpiritRule.matches(it)) ksCount++ }
        }

        // Results
        println("Results:")
        println("Regex:")
        println("  Time:        ${regexTime}ms")
        println("  Valid IPs:   $regexCount")
        println()
        println("KotlinSpirit:")
        println("  Time:        ${ksTime}ms")
        println("  Valid IPs:   $ksCount")
        println()

        val speedup = ksTime.toDouble() / regexTime.toDouble()
        if (speedup > 1.0) {
            println("Regex is ${String.format("%.2f", speedup)}x FASTER")
        } else {
            println("KotlinSpirit is ${String.format("%.2f", 1.0 / speedup)}x FASTER")
        }
    }
}