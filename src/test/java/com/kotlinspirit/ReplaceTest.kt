package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.int
import org.junit.Assert
import org.junit.Test

class ReplaceTest {
    @Test
    fun replaceAllIntegers() {
        val parser = int.compile()
        val result = parser.replaceAll(
            "Hello 3344543, How is your time 456.233 today 321", "eee"
        ).toString()
        Assert.assertEquals("Hello eee, How is your time eee.eee today eee", result)
    }

    @Test
    fun replaceAllNameSplits() {
        val name = char('A'..'Z') + +char('a'..'z')
        val split = name % ','
        val parser = split.compile()
        val result = parser.replaceAll(
            "Hello 3344543, How,Right,Hey is your time No 456.233 today 321", "eee"
        ).toString()
        Assert.assertEquals("eee 3344543, eee is your time eee 456.233 today 321", result)
    }

    @Test
    fun replaceFirstNameSplit() {
        val name = char('A'..'Z') + +char('a'..'z')
        val split = name % ','
        val parser = split.compile()
        val result = parser.replaceFirst(
            "3344543, How,Right,Hey is your time No 456.233 today 321", "eee"
        ).toString()
        Assert.assertEquals("3344543, eee is your time No 456.233 today 321", result)
    }
}