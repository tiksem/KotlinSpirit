package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.space
import com.example.kotlinspirit.Rules.str
import org.junit.Assert
import org.junit.Test

class SplitTest {
    @Test
    fun splitHelloWorld() {
        val r = str('a'..'z', 'A'..'Z') % " "
        val e = r.compile().parseGetResultOrThrow("hello world")
        Assert.assertArrayEquals(e.toTypedArray(), arrayOf("hello", "world"))
    }

    @Test
    fun splitHelloWorld2() {
        var mark: Char = Char.MIN_VALUE
        var array = arrayOf<CharSequence>()
        val r = (str('a'..'z', 'A'..'Z') % " ") {
            array = it.toTypedArray()
        } + char('!', '?').invoke {
            mark = it
        }
        r.compile().matchOrThrow("Hello world!")
        Assert.assertArrayEquals(array, arrayOf("Hello", "world"))
        Assert.assertEquals(mark, '!')
    }

    @Test
    fun words() {
        val r = +(char - space) % space
        Assert.assertArrayEquals(r.compile().parseGetResultOrThrow("hi my dear friend")
            .map { it.toString() }.toTypedArray(), arrayOf("hi", "my", "dear", "friend"))
        Assert.assertArrayEquals(r.compile().parseGetResultOrThrow("hi my dear  friend")
            .map { it.toString() }.toTypedArray(), arrayOf("hi", "my", "dear"))
        Assert.assertEquals(r.compile().tryParse(" hi my dear  friend"), null)
    }
}