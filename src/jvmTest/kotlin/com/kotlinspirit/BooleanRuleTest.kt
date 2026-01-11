package com.kotlinspirit

import com.kotlinspirit.core.Box
import com.kotlinspirit.core.NullBox
import com.kotlinspirit.core.Rules.boolean
import com.kotlinspirit.core.Rules.grammar
import com.kotlinspirit.core.Rules.str
import org.junit.Assert
import org.junit.Test

class BooleanRuleTest {
    private val r = boolean.compile()
    private val reverseR = grammar(
        defineRule = { result ->
            str("yo").requiresPrefix(
                boolean { result.value = it }
            )
        },
        getResult = { it.value },
        dataFactory = { Box(false) }
    ).compile()

    @Test
    fun testTrue() {
        Assert.assertEquals(r.parseGetResultOrThrow("true"), true)
        Assert.assertEquals(r.parse("trueyo").seek, "true".length)
    }

    @Test
    fun testFalse() {
        Assert.assertEquals(r.parseGetResultOrThrow("false"), false)
        Assert.assertEquals(r.parse("falseyo").seek, "false".length)
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