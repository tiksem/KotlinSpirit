package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.Rules.long
import com.kotlinspirit.core.ParseResult
import org.junit.Assert
import org.junit.Test

class LongTest {
    @Test
    fun zero() {
        Assert.assertEquals(0, long.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun minusZero() {
        Assert.assertEquals(0, long.compile().parseGetResultOrThrow("-0"))
    }

    @Test
    fun plusZero() {
        Assert.assertEquals(0, long.compile().parseGetResultOrThrow("+0"))
    }

    @Test
    fun minus() {
        Assert.assertEquals(-345, long.compile().parseGetResultOrThrow("-345"))
    }

    @Test
    fun plus() {
        Assert.assertEquals(345, long.compile().parseGetResultOrThrow("+345"))
    }

    @Test
    fun default() {
        Assert.assertEquals(23523454, long.compile().parseGetResultOrThrow("23523454"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<Long>()
        long.parseWithResult(0, "9223372036854775808", result)
        Assert.assertEquals(ParseCode.LONG_OUT_OF_BOUNDS, result.parseResult.parseCode)
    }

    @Test
    fun noLong() {
        val r = (!long).compile()
        Assert.assertEquals(r.matchesAtBeginning("+dsds"), true)
//        Assert.assertEquals(r.matchesAtBeginning("-dsdsds"), true)
//        Assert.assertEquals(r.matchesAtBeginning("dsdsds"), true)
//        Assert.assertEquals(r.matchesAtBeginning("-"), true)
//        Assert.assertEquals(r.matchesAtBeginning("+"), true)
//        Assert.assertEquals(r.matchesAtBeginning("+4"), false)
//        Assert.assertEquals(r.matchesAtBeginning("-0"), false)
//        Assert.assertEquals(r.matchesAtBeginning("0"), false)
//        Assert.assertEquals(r.matchesAtBeginning("0345"), false)
//        Assert.assertEquals(r.matchesAtBeginning("456"), false)
//        Assert.assertEquals(r.matchesAtBeginning(""), true)
    }
}