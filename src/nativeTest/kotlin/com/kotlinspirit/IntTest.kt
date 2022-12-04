package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.int
import kotlin.test.Test
import kotlin.test.assertEquals

class IntTest {
    @Test
    fun startedWithZero() {
        assertEquals(int.compile().parseWithResult("034534554").errorCode, ParseCode.INT_STARTED_FROM_ZERO)
    }

    @Test
    fun zero() {
        assertEquals(0, int.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun minusZero() {
        assertEquals(0, int.compile().parseGetResultOrThrow("-0"))
    }

    @Test
    fun plusZero() {
        assertEquals(0, int.compile().parseGetResultOrThrow("+0"))
    }

    @Test
    fun minus() {
        assertEquals(-345, int.compile().parseGetResultOrThrow("-345"))
    }

    @Test
    fun plus() {
        assertEquals(345, int.compile().parseGetResultOrThrow("+345"))
    }

    @Test
    fun default() {
        assertEquals(23523454, int.compile().parseGetResultOrThrow("23523454"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<Int>()
        int.parseWithResult(0, "21474836473", result)
        assertEquals(ParseCode.INT_OUT_OF_BOUNDS, result.parseResult.getParseCode())
    }

    @Test
    fun invalid() {
        val result = ParseResult<Int>()
        int.parseWithResult(0, "dsds65537", result)
        assertEquals(ParseCode.INVALID_INT, result.parseResult.getParseCode())
    }

    @Test
    fun noInt() {
        val r = (!int).compile()
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