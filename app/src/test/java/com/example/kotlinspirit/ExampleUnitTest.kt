package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.int
import com.example.kotlinspirit.Rules.quotedString
import com.example.kotlinspirit.Rules.str
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun parseStringStringPair() {
        var first: CharSequence = ""
        var second: CharSequence = ""

        val r = (quotedString {
            first = it
        } + ": " + quotedString {
            second = it
        }).transform {
            Pair(first, second)
        }

        val e = r.parseOrThrow("\"Ivan\": \"privet%1234#\"")

        assertEquals(e.first, "Ivan")
        assertEquals(e.second, "privet%1234#")
    }

    @Test
    fun testStringIntPair() {
        var first: CharSequence = ""
        var second = Int.MAX_VALUE

        val r = (quotedString {
            first = it
        } + ": " + int.on {
            second = it
        }).transform {
            Pair(first, second)
        }

        val e = r.parseOrThrow("\"Ivan\": 23432543")

        assertEquals(e.first, "Ivan")
        assertEquals(e.second, 23432543)
    }
}