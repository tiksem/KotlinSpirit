package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.space
import com.kotlinspirit.core.Rules.str
import org.junit.Assert
import org.junit.Test

class SplitTest {
    @Test
    fun splitHelloWorld() {
        val r = str('a'..'z', 'A'..'Z') % " "
        val e = r.compile().parseGetResultOrThrow("hello world")
        assertArrayEquals(e.toTypedArray(), arrayOf("hello", "world"))
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
        assertArrayEquals(array, arrayOf("Hello", "world"))
        assertEquals(mark, '!')
    }

    @Test
    fun words() {
        val r = +(char - space) % space
        assertArrayEquals(r.compile().parseGetResultOrThrow("hi my dear friend")
            .map { it.toString() }.toTypedArray(), arrayOf("hi", "my", "dear", "friend"))
        assertArrayEquals(r.compile().parseGetResultOrThrow("hi my dear  friend")
            .map { it.toString() }.toTypedArray(), arrayOf("hi", "my", "dear"))
        assertEquals(r.compile().tryParse(" hi my dear  friend"), null)
    }
}