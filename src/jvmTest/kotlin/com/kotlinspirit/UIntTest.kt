package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.uint
import org.junit.Assert
import org.junit.Test

class UIntTest {
    @Test
    fun zero() {
        Assert.assertEquals(0u, uint.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun default() {
        Assert.assertEquals(23523454u, uint.compile().parseGetResultOrThrow("23523454"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<UInt>()
        uint.parseWithResult(0, "4294967296", result)
        Assert.assertEquals(ParseCode.UINT_OUT_OF_BOUNDS, result.parseResult.parseCode)
    }

    @Test
    fun invalid() {
        val result = ParseResult<UInt>()
        uint.parseWithResult(0, "dsds65537", result)
        Assert.assertEquals(ParseCode.INVALID_UINT, result.parseResult.parseCode)
    }

    @Test
    fun invalid2() {
        val result = ParseResult<UInt>()
        uint.parseWithResult(0, "-65537", result)
        Assert.assertEquals(ParseCode.INVALID_UINT, result.parseResult.parseCode)
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
        Assert.assertEquals(r.matchesAtBeginning("0345"), false)
        Assert.assertEquals(r.matchesAtBeginning("456"), false)
        Assert.assertEquals(r.matchesAtBeginning(""), true)
    }
}