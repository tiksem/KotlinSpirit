package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.uint
import org.junit.Assert
import org.junit.Test

class UIntTest {
    @Test
    fun startedWithZero() {
        assertThrows(ParseException::class.java) {
            uint.compile().parseGetResultOrThrow("034534554")
        }
    }

    @Test
    fun zero() {
        assertEquals(0u, uint.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun default() {
        assertEquals(23523454u, uint.compile().parseGetResultOrThrow("23523454"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<UInt>()
        uint.parseWithResult(0, "4294967296", result)
        assertEquals(ParseCode.UINT_OUT_OF_BOUNDS, result.parseResult.getParseCode())
    }

    @Test
    fun invalid() {
        val result = ParseResult<UInt>()
        uint.parseWithResult(0, "dsds65537", result)
        assertEquals(ParseCode.INVALID_UINT, result.parseResult.getParseCode())
    }

    @Test
    fun invalid2() {
        val result = ParseResult<UInt>()
        uint.parseWithResult(0, "-65537", result)
        assertEquals(ParseCode.INVALID_UINT, result.parseResult.getParseCode())
    }

    @Test
    fun noInt() {
        val r = (!uint).compile()
        assertEquals(r.matchesAtBeginning("+dsds"), true)
        assertEquals(r.matchesAtBeginning("-dsdsds"), true)
        assertEquals(r.matchesAtBeginning("dsdsds"), true)
        assertEquals(r.matchesAtBeginning("-"), true)
        assertEquals(r.matchesAtBeginning("+"), true)
        assertEquals(r.matchesAtBeginning("+4"), true)
        assertEquals(r.matchesAtBeginning("-0"), true)
        assertEquals(r.matchesAtBeginning("0"), false)
        assertEquals(r.matchesAtBeginning("0345"), true)
        assertEquals(r.matchesAtBeginning("456"), false)
        assertEquals(r.matchesAtBeginning(""), true)
    }
}