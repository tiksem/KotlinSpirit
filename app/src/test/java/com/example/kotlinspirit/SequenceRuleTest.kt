package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.double
import org.junit.Assert
import org.junit.Test

class SequenceRuleTest {
    @Test
    fun testQuotedDouble() {
        val r = '(' + double + ')'
        r.compile().parseOrThrow("(4.5)")
    }

    @Test
    fun testDoubleAfterBracket() {
        val r = '(' + double
        Assert.assertEquals(r.compile().tryParse("(4.5"), "(4.5".length)
    }
}