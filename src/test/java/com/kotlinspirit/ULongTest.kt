package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseException
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rules.ulong
import com.kotlinspirit.core.getParseCode
import org.junit.Assert
import org.junit.Test

class ULongTest {
    @Test
    fun startedWithZero() {
        Assert.assertThrows(ParseException::class.java) {
            ulong.compile().parseGetResultOrThrow("034534554")
        }
    }

    @Test
    fun zero() {
        Assert.assertEquals(0.toULong(), ulong.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun default() {
        Assert.assertEquals(23523454.toULong(), ulong.debug().compile().parseGetResultOrThrow("23523454"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<ULong>()
        ulong.parseWithResult(0, "18446744073709551616", result)
        Assert.assertEquals(ParseCode.ULONG_OUT_OF_BOUNDS, result.parseResult.getParseCode())
    }

    @Test
    fun noInt() {
        val r = (!ulong).compile()
        Assert.assertEquals(r.matchesAtBeginning("+dsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("-dsdsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("dsdsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("-"), true)
        Assert.assertEquals(r.matchesAtBeginning("+"), true)
        Assert.assertEquals(r.matchesAtBeginning("+4"), true)
        Assert.assertEquals(r.matchesAtBeginning("-0"), true)
        Assert.assertEquals(r.matchesAtBeginning("0"), false)
        Assert.assertEquals(r.matchesAtBeginning("0345"), true)
        Assert.assertEquals(r.matchesAtBeginning("456"), false)
        Assert.assertEquals(r.matchesAtBeginning(""), true)
    }
}