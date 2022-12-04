package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.core.Rules.short
import org.junit.Assert
import org.junit.Test

class ShortTest {
    @Test
    fun startedWithZero() {
        assertThrows(ParseException::class.java) {
            short.compile().parseGetResultOrThrow("0345")
        }
    }

    @Test
    fun zero() {
        assertEquals(0.toShort(), short.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun minusZero() {
        assertEquals(0.toShort(), short.compile().parseGetResultOrThrow("-0"))
    }

    @Test
    fun plusZero() {
        assertEquals(0.toShort(), short.compile().parseGetResultOrThrow("+0"))
    }

    @Test
    fun minus() {
        assertEquals((-345).toShort(), short.compile().parseGetResultOrThrow("-345"))
    }

    @Test
    fun plus() {
        assertEquals(345.toShort(), short.compile().parseGetResultOrThrow("+345"))
    }

    @Test
    fun default() {
        assertEquals(32767.toShort(), short.compile().parseGetResultOrThrow("32767"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<Short>()
        short.parseWithResult(0, "32768", result)
        assertEquals(ParseCode.SHORT_OUT_OF_BOUNDS, result.parseResult.getParseCode())
    }

    @Test
    fun invalid() {
        val result = ParseResult<Short>()
        short.parseWithResult(0, "dsds65537", result)
        assertEquals(ParseCode.INVALID_SHORT, result.parseResult.getParseCode())
    }

    @Test
    fun noInt() {
        val r = (!short).compile()
        assertEquals(r.matchesAtBeginning("+dsds"), true)
        assertEquals(r.matchesAtBeginning("-dsdsds"), true)
        assertEquals(r.matchesAtBeginning("dsdsds"), true)
        assertEquals(r.matchesAtBeginning("-"), true)
        assertEquals(r.matchesAtBeginning("+"), true)
        assertEquals(r.matchesAtBeginning("+4"), false)
        assertEquals(r.matchesAtBeginning("-0"), false)
        assertEquals(r.matchesAtBeginning("0"), false)
        assertEquals(r.matchesAtBeginning("0345"), true)
        assertEquals(r.matchesAtBeginning("456"), false)
        assertEquals(r.matchesAtBeginning(""), true)
    }
}