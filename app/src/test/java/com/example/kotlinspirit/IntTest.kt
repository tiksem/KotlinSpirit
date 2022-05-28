package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.int
import com.example.kotlinspirit.Rules.uint
import org.junit.Assert
import org.junit.Test

class IntTest {
    @Test
    fun startedWithZero() {
        Assert.assertThrows(ParseException::class.java) {
            int.parseOrThrow("034534554")
        }
    }

    @Test
    fun zero() {
        Assert.assertEquals(0, int.parseOrThrow("0"))
    }

    @Test
    fun minusZero() {
        Assert.assertEquals(0, int.parseOrThrow("-0"))
    }

    @Test
    fun minus() {
        Assert.assertEquals(-345, int.parseOrThrow("-345"))
    }

    @Test
    fun default() {
        Assert.assertEquals(23523454, int.parseOrThrow("23523454"))
    }

    @Test
    fun outOfRange() {
        val state = ParseState()
        int.parse(state, "21474836473")
        Assert.assertEquals(StepCode.INT_OUT_OF_RANGE, state.parseCode)
    }

    @Test
    fun uint() {
        Assert.assertEquals(345, uint.parseOrThrow("345"))
    }

    @Test
    fun uintFailed() {
        Assert.assertThrows(ParseException::class.java) {
            uint.parseOrThrow("-345")
        }
    }
}