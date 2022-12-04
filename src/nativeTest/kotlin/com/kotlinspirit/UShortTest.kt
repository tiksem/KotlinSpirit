package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.ushort
import org.junit.Assert
import org.junit.Test

class UShortTest {
    @Test
    fun startedWithZero() {
        assertThrows(ParseException::class.java) {
            ushort.compile().parseGetResultOrThrow("034534554")
        }
    }

    @Test
    fun zero() {
        assertEquals(0u.toUShort(), ushort.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun default() {
        assertEquals(2352u.toUShort(), ushort.compile().parseGetResultOrThrow("2352"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<UShort>()
        ushort.parseWithResult(0, "65537", result)
        assertEquals(ParseCode.USHORT_OUT_OF_BOUNDS, result.parseResult.getParseCode())
    }

    @Test
    fun invalid() {
        val result = ParseResult<UShort>()
        ushort.parseWithResult(0, "dsds65537", result)
        assertEquals(ParseCode.INVALID_USHORT, result.parseResult.getParseCode())
    }

    @Test
    fun invalid2() {
        val result = ParseResult<UShort>()
        Rules.ushort.parseWithResult(0, "-3434", result)
        assertEquals(ParseCode.INVALID_USHORT, result.parseResult.getParseCode())
    }

    @Test
    fun noInt() {
        val r = (!ushort).compile()
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