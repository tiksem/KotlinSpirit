package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.Rules.long
import com.kotlinspirit.core.ParseException
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.getParseCode
import org.junit.Assert
import org.junit.Test

class LongTest {
    @Test
    fun startedWithZero() {
        assertThrows(ParseException::class.java) {
            long.compile().parseGetResultOrThrow("034534554")
        }
    }

    @Test
    fun zero() {
        assertEquals(0, long.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun minusZero() {
        assertEquals(0, long.compile().parseGetResultOrThrow("-0"))
    }

    @Test
    fun plusZero() {
        assertEquals(0, long.compile().parseGetResultOrThrow("+0"))
    }

    @Test
    fun minus() {
        assertEquals(-345, long.compile().parseGetResultOrThrow("-345"))
    }

    @Test
    fun plus() {
        assertEquals(345, long.compile().parseGetResultOrThrow("+345"))
    }

    @Test
    fun default() {
        assertEquals(23523454, long.compile().parseGetResultOrThrow("23523454"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<Long>()
        long.parseWithResult(0, "9223372036854775808", result)
        assertEquals(ParseCode.INT_OUT_OF_BOUNDS, result.parseResult.getParseCode())
    }

    @Test
    fun noLong() {
        val r = (!long).compile()
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