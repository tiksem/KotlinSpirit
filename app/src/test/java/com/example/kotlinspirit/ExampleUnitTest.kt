package com.example.kotlinspirit

import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun splitHelloWorld() {
        val r = str('a'..'z', 'A'..'Z') % " "
        val e = r.parseOrThrow("hello world")
        assertArrayEquals(e.toTypedArray(), arrayOf("hello", "world"))
    }

    @Test
    fun splitHelloWorld2() {
        var mark: Char = Char.MIN_VALUE
        var array = arrayOf<CharSequence>()
        val r = (str('a'..'z', 'A'..'Z') % " ").on {
            array = it.toTypedArray()
        } + char('!', '?').on {
            mark = it
        }
        r.parseOrThrow("Hello world!")
        assertArrayEquals(array, arrayOf("Hello", "world"))
        assertEquals(mark, '!')
    }

    @Test
    fun parsePair() {
        var first: CharSequence = ""
        var second: CharSequence = ""

        val quote = char('"', '\'')
        fun quotedString(callback: (CharSequence) -> Unit): Rule<CharSequence> {
            return quote + str {
                it != '"' && it != '\''
            }.on(success = callback) + quote
        }

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
}