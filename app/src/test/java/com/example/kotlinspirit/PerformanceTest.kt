package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.digit
import com.example.kotlinspirit.Rules.oneOf
import org.junit.Test
import kotlin.random.Random

class PerformanceTest {
    @Test
    fun test1() {
        val pattern = Regex("(?:[AZ]+|(?:Ivan|Vasil|Eblan)+)@[09]+")
        val parser = (
                (oneOf("Ivan", "Vasil", "Eblan") or char('A'..'Z'))
                    .repeat().asString() + '@' + digit.repeat()
                )
            .compile()

        val input = StringBuilder()
        val arr = arrayOf("Ivan", "Vasil", "Eblan")
        repeat(5000000) {
            if (Random.nextBoolean()) {
                input.append(Random.nextInt('A'.code, 'Z'.code + 1).toChar())
            } else {
                input.append(arr.random())
            }
        }
        input.append('@')
        repeat(5000000) {
            input.append(Random.nextInt(0, 10).digitToChar())
        }

        var time = System.currentTimeMillis()
        parser.parseOrThrow(input)
        System.out.println(System.currentTimeMillis() - time)
        time = System.currentTimeMillis()
        pattern.find(input)
        System.out.println(System.currentTimeMillis() - time)
    }
}