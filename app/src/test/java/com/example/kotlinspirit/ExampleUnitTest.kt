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
    fun splitTest() {
        val r = str('a'..'z', 'A'..'Z') % " "
        val e = r.parseOrThrow("hello world")
        assertArrayEquals(e.toTypedArray(), arrayOf("hello", "world"))
    }
}