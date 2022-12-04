package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.Rules.char
import kotlin.test.Test
import kotlin.test.assertEquals

class ExactCharRuleTest {
    @Test
    fun test1() {
        val r = char('a').compile()
        assertEquals(r.tryParse("abcdef"), 1)
        assertEquals(r.parse("").errorCode, ParseCode.EOF)
        assertEquals(r.parse("bcdaaef").errorCode, ParseCode.CHAR_PREDICATE_FAILED)
    }

    @Test
    fun noTest() {
        val r = (!char('a')).compile()
        assertEquals(r.tryParse("abcdef"), null)
        assertEquals(r.tryParse(""), 0)
        assertEquals(r.tryParse("bcdaaef"), 1)
    }

    @Test
    fun diff() {
        val r = (char - 'a').compile()
        assertEquals(r.tryParse("abcdef"), null)
        assertEquals(r.tryParse(""), null)
        assertEquals(r.tryParse("bcdaaef"), 1)
    }

    @Test
    fun diff2() {
        val r = (char('a'..'z') - char('e'..'g')).compile()
        assertEquals(r.tryParse("abcdef"), 1)
        assertEquals(r.tryParse("edfdfdf33"), null)
        assertEquals(r.tryParse(""), null)
    }

    @Test
    fun diff3() {
        val r = (char('a'..'z') - char('e', 'z', 'g')).compile()
        assertEquals(r.tryParse("kbcdef"), 1)
        assertEquals(r.tryParse("edfdfdf33"), null)
        assertEquals(r.tryParse("zdfdfdf33"), null)
        assertEquals(r.tryParse("gdfdfdf33"), null)
        assertEquals(r.tryParse(""), null)
    }
}