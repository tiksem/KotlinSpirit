package com.kotlinspirit

import com.kotlinspirit.core.Rules.boolean
import com.kotlinspirit.core.Rules.str
import com.kotlinspirit.grammar.nestedResult
import org.junit.Assert
import org.junit.Test

class BooleanRuleTest {
    private val r = boolean.compile()
    private val reverseR = nestedResult(
        nested = boolean,
        entire = {
            str("yo").requiresPrefix(it)
        }
    ).compile()

    @Test
    fun testTrue() {
        Assert.assertEquals(r.parseGetResultOrThrow("true"), true)
        Assert.assertEquals(r.parse("trueyo").endSeek, "true".length)
    }

    @Test
    fun testFalse() {
        Assert.assertEquals(r.parseGetResultOrThrow("false"), false)
        Assert.assertEquals(r.parse("falseyo").endSeek, "false".length)
    }

    @Test
    fun testTrueReverse() {
        Assert.assertEquals(reverseR.findFirst("trueyo"), true)
        Assert.assertEquals(reverseR.findFirst("uutrueyo"), true)
        Assert.assertEquals(r.findFirst("uuerueyo"), null)
    }

    @Test
    fun testFalseReverse() {
        Assert.assertEquals(reverseR.findFirst("falseyo"), false)
        Assert.assertEquals(reverseR.findFirst("uufalseyo"), false)
        Assert.assertEquals(reverseR.findFirst("uuealseyo"), null)
    }
}