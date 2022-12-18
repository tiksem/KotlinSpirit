package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.int
import org.junit.Assert
import org.junit.Test

class IntTest {
    @Test
    fun zero() {
        Assert.assertEquals(0, int.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun minusZero() {
        Assert.assertEquals(0, int.compile().parseGetResultOrThrow("-0"))
    }

    @Test
    fun plusZero() {
        Assert.assertEquals(0, int.compile().parseGetResultOrThrow("+0"))
    }

    @Test
    fun minus() {
        Assert.assertEquals(-345, int.compile().parseGetResultOrThrow("-345"))
    }

    @Test
    fun plus() {
        Assert.assertEquals(345, int.compile().parseGetResultOrThrow("+345"))
    }

    @Test
    fun default() {
        Assert.assertEquals(23523454, int.compile().parseGetResultOrThrow("23523454"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<Int>()
        int.parseWithResult(0, "21474836473", result)
        Assert.assertEquals(ParseCode.INT_OUT_OF_BOUNDS, result.parseResult.getParseCode())
    }

    @Test
    fun invalid() {
        val result = ParseResult<Int>()
        int.parseWithResult(0, "dsds65537", result)
        Assert.assertEquals(ParseCode.INVALID_INT, result.parseResult.getParseCode())
    }

    @Test
    fun noInt() {
        val r = (!int).compile()
        Assert.assertEquals(r.matchesAtBeginning("+dsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("-dsdsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("dsdsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("-"), true)
        Assert.assertEquals(r.matchesAtBeginning("+"), true)
        Assert.assertEquals(r.matchesAtBeginning("+4"), false)
        Assert.assertEquals(r.matchesAtBeginning("-0"), false)
        Assert.assertEquals(r.matchesAtBeginning("0"), false)
        Assert.assertEquals(r.matchesAtBeginning("0345"), false)
        Assert.assertEquals(r.matchesAtBeginning("456"), false)
        Assert.assertEquals(r.matchesAtBeginning(""), true)
    }
}