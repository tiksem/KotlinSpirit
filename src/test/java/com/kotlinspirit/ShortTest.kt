package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.core.Rules.short
import org.junit.Assert
import org.junit.Test

class ShortTest {
    @Test
    fun startedWithZero() {
        Assert.assertThrows(ParseException::class.java) {
            short.compile().parseGetResultOrThrow("0345")
        }
    }

    @Test
    fun zero() {
        Assert.assertEquals(0.toShort(), short.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun minusZero() {
        Assert.assertEquals(0.toShort(), short.compile().parseGetResultOrThrow("-0"))
    }

    @Test
    fun plusZero() {
        Assert.assertEquals(0.toShort(), short.compile().parseGetResultOrThrow("+0"))
    }

    @Test
    fun minus() {
        Assert.assertEquals((-345).toShort(), short.compile().parseGetResultOrThrow("-345"))
    }

    @Test
    fun plus() {
        Assert.assertEquals(345.toShort(), short.compile().parseGetResultOrThrow("+345"))
    }

    @Test
    fun default() {
        Assert.assertEquals(32767.toShort(), short.compile().parseGetResultOrThrow("32767"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<Short>()
        short.parseWithResult(0, "32768", result)
        Assert.assertEquals(ParseCode.SHORT_OUT_OF_BOUNDS, result.parseResult.getParseCode())
    }

    @Test
    fun invalid() {
        val result = ParseResult<Short>()
        short.parseWithResult(0, "dsds65537", result)
        Assert.assertEquals(ParseCode.INVALID_SHORT, result.parseResult.getParseCode())
    }

    @Test
    fun noInt() {
        val r = (!short).compile()
        Assert.assertEquals(r.matchesAtBeginning("+dsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("-dsdsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("dsdsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("-"), true)
        Assert.assertEquals(r.matchesAtBeginning("+"), true)
        Assert.assertEquals(r.matchesAtBeginning("+4"), false)
        Assert.assertEquals(r.matchesAtBeginning("-0"), false)
        Assert.assertEquals(r.matchesAtBeginning("0"), false)
        Assert.assertEquals(r.matchesAtBeginning("0345"), true)
        Assert.assertEquals(r.matchesAtBeginning("456"), false)
        Assert.assertEquals(r.matchesAtBeginning(""), true)
    }
}