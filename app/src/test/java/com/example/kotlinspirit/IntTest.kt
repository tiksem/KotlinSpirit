package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.int
import org.junit.Assert
import org.junit.Test

class IntTest {
    @Test
    fun startedWithZero() {
        Assert.assertThrows(ParseException::class.java) {
            int.parseGetResultOrThrow("034534554")
        }
    }

    @Test
    fun zero() {
        Assert.assertEquals(0, int.parseGetResultOrThrow("0"))
    }

    @Test
    fun minusZero() {
        Assert.assertEquals(0, int.parseGetResultOrThrow("-0"))
    }

    @Test
    fun minus() {
        Assert.assertEquals(-345, int.parseGetResultOrThrow("-345"))
    }

    @Test
    fun default() {
        Assert.assertEquals(23523454, int.parseGetResultOrThrow("23523454"))
    }

    @Test
    fun outOfRange() {
        val result = ParseResult<Int>()
        int.parseWithResult(0, "21474836473", result)
        Assert.assertEquals(ParseCode.INT_OUT_OF_BOUNDS, result.stepResult.getParseCode())
    }
}