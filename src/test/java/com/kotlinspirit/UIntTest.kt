package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseException
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rules.uint
import com.kotlinspirit.core.getParseCode
import org.junit.Assert
import org.junit.Test

class UIntTest {
    @Test
    fun startedWithZero() {
        Assert.assertThrows(ParseException::class.java) {
            uint.compile().parseGetResultOrThrow("034534554")
        }
    }

    @Test
    fun zero() {
        Assert.assertEquals(0u, uint.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun default() {
        Assert.assertEquals(23523454u, uint.debug().compile().parseGetResultOrThrow("23523454"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<UInt>()
        uint.parseWithResult(0, "4294967296", result)
        Assert.assertEquals(ParseCode.UINT_OUT_OF_BOUNDS, result.parseResult.getParseCode())
    }

    @Test
    fun noInt() {
        val r = (!uint).compile()
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