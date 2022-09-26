package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.core.Rules.space
import org.junit.Assert
import org.junit.Test

private val word = char('A'..'Z') + +char('a'..'z')
private val quotedWord = word.quoted('(', ')')
private val quotedInt = int.quoted('(', ')')

class QuotedRuleTest {
    @Test
    fun quotedInt() {
        val r = quotedInt.compile()
        Assert.assertEquals(r.parseGetResultOrThrow("(345)"), 345)
        Assert.assertEquals(r.parseGetResultOrThrow("(0)"), 0)
        Assert.assertEquals(r.parseGetResultOrThrow("(-345)"), -345)

        Assert.assertEquals(r.tryParse("(345)dsds"), "(345)".length)
    }

    @Test
    fun quotedIntOrQuotedWord() {
        val r = (quotedWord or quotedInt).compile()

        Assert.assertEquals(r.parseGetResultOrThrow("(345)"), 345)
        Assert.assertEquals(r.parseGetResultOrThrow("(0)"), 0)
        Assert.assertEquals(r.parseGetResultOrThrow("(-345)"), -345)
        Assert.assertEquals(r.tryParse("(345)dsds"), "(345)".length)

        Assert.assertEquals(r.parseGetResultOrThrow("(Ab)"), "Ab")
        Assert.assertEquals(r.parseGetResultOrThrow("(Eblan)"), "Eblan")
        Assert.assertEquals(r.tryParse("(Asd)dsds"), "(Asd)".length)
    }

    @Test
    fun quotedIntAndWord() {
        val r = (quotedInt + quotedWord).compile()

        Assert.assertEquals(r.tryParse("(345)(Alex)"), "(345)(Alex)".length)
        Assert.assertEquals(r.tryParse("(Alex)(345)"), null)
        Assert.assertEquals(r.tryParse("(345)"), null)
    }

    @Test
    fun quotedIntSplit() {
        val r = (int % ',').quoted('[', ']').compile()
        Assert.assertArrayEquals(
            r.parseGetResultOrThrow("[123,45,1234,-23]").toTypedArray(),
            arrayOf(123,45,1234,-23)
        )
    }

    @Test
    fun quotedIntSplitPlusSkipper() {
        val skipper = space.repeat()
        val divider = char(',').quoted(skipper)
        val r = (int % divider).quoted(skipper + '[' + skipper, skipper + ']' + skipper).compile()
        Assert.assertArrayEquals(
            r.parseGetResultOrThrow("  [  123, 45,   1234,  -23   ] ").toTypedArray(),
            arrayOf(123,45,1234,-23)
        )
    }

    @Test
    fun quotedIntOrWordSplitPlusSkipper() {
        val skipper = space.repeat()
        val divider = char(',').quoted(skipper)
        val r = ((int or word).asString() % divider).quoted(skipper + '[' + skipper, skipper + ']' + skipper).compile()
        Assert.assertArrayEquals(
            r.parseGetResultOrThrow("  [  123, Julia,   1234,  Mordor   ] ").toTypedArray(),
            arrayOf("123","Julia","1234","Mordor")
        )
    }

    @Test
    fun quotedInsideQuotedSplit() {
        val r = (int.quoted(word).asString() % word.quoted(int)).compile()
        Assert.assertArrayEquals(
            r.parseGetResultOrThrow("Julia123Radon12Abdul-456Anton234Simon").toTypedArray(),
            arrayOf("Julia123Radon", "Anton234Simon")
        )
    }
}