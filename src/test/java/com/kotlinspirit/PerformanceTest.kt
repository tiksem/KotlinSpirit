package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.digit
import com.kotlinspirit.core.Rules.double
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.core.Rules.oneOf
import org.junit.Test
import kotlin.random.Random

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
}