package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.charIf
import org.junit.Assert
import org.junit.Test

class CharPredicateRuleTest {
    @Test
    fun test1() {
        val r = char('a', 'b', 'e').compile()
        Assert.assertEquals(r.tryParse("adf"), 1)
        Assert.assertEquals(r.tryParse("edf"), 1)
        Assert.assertEquals(r.tryParse("bde"), 1)
        Assert.assertEquals(r.tryParse("eee"), 1)
        Assert.assertEquals(r.tryParse("uee"), null)
    }

    @Test
    fun test2() {
        val r = char('a'..'u').compile()
        Assert.assertEquals(r.tryParse("xyz"), null)
        Assert.assertEquals(r.tryParse("dfgjdfhjghj"), 1)
        Assert.assertEquals(r.tryParse("zzzzzzzz"), null)
    }

    @Test
    fun test3() {
        val r = char('a'..'u', 'e'..'r').compile()
        Assert.assertEquals(r.tryParse("xyz"), null)
        Assert.assertEquals(r.tryParse("dfgjdfhjghj"), 1)
        Assert.assertEquals(r.tryParse("zzzzzzzz"), null)
    }

    @Test
    fun test4() {
        val r = char('z'..'a').compile()
        Assert.assertEquals(r.tryParse("xyz"), null)
        Assert.assertEquals(r.tryParse("dfgjdfhjghj"), null)
        Assert.assertEquals(r.tryParse("zzzzzzzz"), null)
    }

    @Test
    fun test5() {
        val r = char(ranges = arrayOf('z'..'a'), chars = charArrayOf('a')).compile()
        Assert.assertEquals(r.tryParse("ayz"), 1)
        Assert.assertEquals(r.tryParse("dfgjdfhjghj"), null)
        Assert.assertEquals(r.tryParse("zzzzzzzz"), null)
    }

    @Test
    fun test6() {
        val r = char(ranges = arrayOf('a'..'z'), chars = charArrayOf('a')).compile()
        Assert.assertEquals(r.tryParse("ayz"), 1)
        Assert.assertEquals(r.tryParse("dfgjdfhjghj"), 1)
        Assert.assertEquals(r.tryParse("zzzzzzzz"), 1)
    }

    @Test
    fun test7() {
        val r = charIf {
            it.isDigit()
        }.compile()

        Assert.assertEquals(r.tryParse("ayz"), null)
        Assert.assertEquals(r.tryParse("5ayz"), 1)
    }

    @Test
    fun test8() {
        val r = char(ranges = arrayOf('a'..'z', '0'..'3', 'e'..'d'), chars = charArrayOf('|')).compile()
        Assert.assertEquals(r.tryParse("ayz"), 1)
        Assert.assertEquals(r.tryParse("dfgjdfhjghj"), 1)
        Assert.assertEquals(r.tryParse("zzzzzzzz"), 1)
        Assert.assertEquals(r.tryParse("\\"), null)
        Assert.assertEquals(r.tryParse("|"), 1)
    }

    @Test
    fun noTest1() {
        val r = (!char('a', 'b', 'e')).compile()
        Assert.assertEquals(r.tryParse("adf"), null)
        Assert.assertEquals(r.tryParse("edf"), null)
        Assert.assertEquals(r.tryParse("bde"), null)
        Assert.assertEquals(r.tryParse("eee"), null)
        Assert.assertEquals(r.tryParse("uee"), 1)
    }

    @Test
    fun noTest2() {
        val r = (!char('a'..'u')).compile()
        Assert.assertEquals(r.tryParse("xyz"), 1)
        Assert.assertEquals(r.tryParse("dfgjdfhjghj"), null)
        Assert.assertEquals(r.tryParse("zzzzzzzz"), 1)
    }

    @Test
    fun noTest3() {
        val r = (!char('a'..'u', 'e'..'r')).compile()
        Assert.assertEquals(r.tryParse("xyz"), 1)
        Assert.assertEquals(r.tryParse("dfgjdfhjghj"), null)
        Assert.assertEquals(r.tryParse("zzzzzzzz"), 1)
    }

    @Test
    fun noTest4() {
        val r = (!char('z'..'a')).compile()
        Assert.assertEquals(r.tryParse("xyz"), 1)
        Assert.assertEquals(r.tryParse("dfgjdfhjghj"), 1)
        Assert.assertEquals(r.tryParse("zzzzzzzz"), 1)
    }

    @Test
    fun noTest5() {
        val r = (!char(ranges = arrayOf('z'..'a'), chars = charArrayOf('a'))).compile()
        Assert.assertEquals(r.tryParse("ayz"), null)
        Assert.assertEquals(r.tryParse("dfgjdfhjghj"), 1)
        Assert.assertEquals(r.tryParse("zzzzzzzz"), 1)
    }

    @Test
    fun noTest6() {
        val r = (!char(ranges = arrayOf('a'..'z'), chars = charArrayOf('a'))).compile()
        Assert.assertEquals(r.tryParse("ayz"), null)
        Assert.assertEquals(r.tryParse("dfgjdfhjghj"), null)
        Assert.assertEquals(r.tryParse("zzzzzzzz"), null)
    }

    @Test
    fun noTest7() {
        val r = (!charIf {
            it.isDigit()
        }).compile()

        Assert.assertEquals(r.tryParse("ayz"), 1)
        Assert.assertEquals(r.tryParse("5ayz"), null)
    }

    @Test
    fun noTest8() {
        val r = (!char(ranges = arrayOf('a'..'z', '0'..'3', 'e'..'d'), chars = charArrayOf('|'))).compile()
        Assert.assertEquals(r.tryParse("ayz"), null)
        Assert.assertEquals(r.tryParse("dfgjdfhjghj"), null)
        Assert.assertEquals(r.tryParse("zzzzzzzz"), null)
        Assert.assertEquals(r.tryParse("\\"), 1)
        Assert.assertEquals(r.tryParse("|"), null)
    }
}