package com.example.kotlinspirit

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
}