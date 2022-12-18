package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.ushort
import org.junit.Assert
import org.junit.Test

class UShortTest {
    @Test
    fun startedWithZero() {
        Assert.assertThrows(ParseException::class.java) {
            ushort.compile().parseGetResultOrThrow("034534554")
        }
    }

    @Test
    fun zero() {
        Assert.assertEquals(0u.toUShort(), ushort.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun default() {
        Assert.assertEquals(2352u.toUShort(), ushort.compile().parseGetResultOrThrow("2352"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<UShort>()
        ushort.parseWithResult(0, "65537", result)
        Assert.assertEquals(ParseCode.USHORT_OUT_OF_BOUNDS, result.parseResult.getParseCode())
    }

    @Test
    fun invalid() {
        val result = ParseResult<UShort>()
        ushort.parseWithResult(0, "dsds65537", result)
        Assert.assertEquals(ParseCode.INVALID_USHORT, result.parseResult.getParseCode())
    }

    @Test
    fun invalid2() {
        val result = ParseResult<UShort>()
        Rules.ushort.parseWithResult(0, "-3434", result)
        Assert.assertEquals(ParseCode.INVALID_USHORT, result.parseResult.getParseCode())
    }

    @Test
    fun noInt() {
        val r = (!ushort).compile()
        Assert.assertEquals(r.matchesAtBeginning("+dsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("-dsdsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("dsdsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("-"), true)
        Assert.assertEquals(r.matchesAtBeginning("+"), true)
        Assert.assertEquals(r.matchesAtBeginning("+4"), true)
        Assert.assertEquals(r.matchesAtBeginning("-0"), true)
        Assert.assertEquals(r.matchesAtBeginning("0"), false)
        Assert.assertEquals(r.matchesAtBeginning("0345"), false)
        Assert.assertEquals(r.matchesAtBeginning("456"), false)
        Assert.assertEquals(r.matchesAtBeginning(""), true)
    }
}