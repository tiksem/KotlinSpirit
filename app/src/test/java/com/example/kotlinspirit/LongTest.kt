package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.long
import org.junit.Assert
import org.junit.Test

class LongTest {
    @Test
    fun startedWithZero() {
        Assert.assertThrows(ParseException::class.java) {
            long.compile().parseGetResultOrThrow("034534554")
        }
    }

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
        Assert.assertEquals(ParseCode.INT_OUT_OF_BOUNDS, result.parseResult.getParseCode())
    }

    @Test
    fun noLong() {
        val r = (!long).compile()
        r.matchOrThrow("+dsds")
        r.matchOrThrow("-dsdsds")
        r.matchOrThrow("dsdsds")
        r.matchOrThrow("-")
        r.matchOrThrow("+")
    }
}