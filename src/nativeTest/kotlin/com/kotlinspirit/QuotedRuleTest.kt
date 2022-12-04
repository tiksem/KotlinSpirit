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
        assertEquals(r.parseGetResultOrThrow("(345)"), 345)
        assertEquals(r.parseGetResultOrThrow("(0)"), 0)
        assertEquals(r.parseGetResultOrThrow("(-345)"), -345)

        assertEquals(r.tryParse("(345)dsds"), "(345)".length)
    }

    @Test
    fun quotedIntOrQuotedWord() {
        val r = (quotedWord or quotedInt).compile()

        assertEquals(r.parseGetResultOrThrow("(345)"), 345)
        assertEquals(r.parseGetResultOrThrow("(0)"), 0)
        assertEquals(r.parseGetResultOrThrow("(-345)"), -345)
        assertEquals(r.tryParse("(345)dsds"), "(345)".length)

        assertEquals(r.parseGetResultOrThrow("(Ab)"), "Ab")
        assertEquals(r.parseGetResultOrThrow("(Eblan)"), "Eblan")
        assertEquals(r.tryParse("(Asd)dsds"), "(Asd)".length)
    }

    @Test
    fun quotedIntAndWord() {
        val r = (quotedInt + quotedWord).compile()

        assertEquals(r.tryParse("(345)(Alex)"), "(345)(Alex)".length)
        assertEquals(r.tryParse("(Alex)(345)"), null)
        assertEquals(r.tryParse("(345)"), null)
    }

    @Test
    fun quotedIntSplit() {
        val r = (int % ',').quoted('[', ']').compile()
        val str = "[123,45,1234,-23]"
        val res = r.parseWithResult(str)
        assertArrayEquals(
            res.data!!.toTypedArray(),
            arrayOf(123,45,1234,-23)
        )
        assertEquals(res.endSeek, str.length)
    }

    @Test
    fun quotedIntSplitPlusSkipper() {
        val skipper = space.repeat()
        val divider = char(',').quoted(skipper)
        val r = (int % divider).quoted(skipper + '[' + skipper, skipper + ']' + skipper).compile()
        val str = "  [  123, 45,   1234,  -23   ] "
        val res = r.parseWithResult(str)
        assertArrayEquals(
            res.data!!.toTypedArray(),
            arrayOf(123,45,1234,-23)
        )
        assertEquals(res.endSeek, str.length)
    }

    @Test
    fun quotedIntOrWordSplitPlusSkipper() {
        val skipper = space.repeat()
        val divider = char(',').quoted(skipper)
        val r = ((int or word).asString() % divider).quoted(skipper + '[' + skipper, skipper + ']' + skipper).compile()
        val str = "  [  123, Julia,   1234,  Mordor   ] "
        val res = r.parseWithResult(str)
        assertArrayEquals(
            res.data!!.toTypedArray(),
            arrayOf("123","Julia","1234","Mordor")
        )
        assertEquals(res.endSeek, str.length)
    }

    @Test
    fun quotedInsideQuotedSplit() {
        val r = (int.quoted(word).asString() % word.quoted(int)).compile()
        val str = "Julia123Radon12Abdul-456Anton234Simon"
        val res = r.parseWithResult(str)
        assertArrayEquals(
            res.data!!.toTypedArray(),
            arrayOf("Julia123Radon", "Anton234Simon")
        )
        assertEquals(res.endSeek, str.length)
    }
}