package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import org.junit.Assert
import org.junit.Test

class ExactCharRuleTest {
    @Test
    fun test1() {
        val r = char('a').compile()
        Assert.assertEquals(r.tryParse("abcdef"), 1)
        Assert.assertEquals(r.parse("").errorCode, ParseCode.EOF)
        Assert.assertEquals(r.parse("bcdaaef").errorCode, ParseCode.CHAR_PREDICATE_FAILED)
    }

    @Test
    fun noTest() {
        val r = (!char('a')).compile()
        Assert.assertEquals(r.tryParse("abcdef"), null)
        Assert.assertEquals(r.tryParse(""), 0)
        Assert.assertEquals(r.tryParse("bcdaaef"), 1)
    }

    @Test
    fun diff() {
        val r = (char - 'a').compile()
        Assert.assertEquals(r.tryParse("abcdef"), null)
        Assert.assertEquals(r.tryParse(""), null)
        Assert.assertEquals(r.tryParse("bcdaaef"), 1)
    }

    @Test
    fun diff2() {
        val r = (char('a'..'z') - char('e'..'g')).compile()
        Assert.assertEquals(r.tryParse("abcdef"), 1)
        Assert.assertEquals(r.tryParse("edfdfdf33"), null)
        Assert.assertEquals(r.tryParse(""), null)
    }

    @Test
    fun diff3() {
        val r = (char('a'..'z') - char('e', 'z', 'g')).compile()
        Assert.assertEquals(r.tryParse("kbcdef"), 1)
        Assert.assertEquals(r.tryParse("edfdfdf33"), null)
        Assert.assertEquals(r.tryParse("zdfdfdf33"), null)
        Assert.assertEquals(r.tryParse("gdfdfdf33"), null)
        Assert.assertEquals(r.tryParse(""), null)
    }
}