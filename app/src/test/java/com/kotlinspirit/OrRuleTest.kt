package com.kotlinspirit

import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.core.Rules.latinStr
import org.junit.Assert
import org.junit.Test

class OrRuleTest {
    @Test
    fun intOrString() {
        val r = int or latinStr
        Assert.assertEquals(242343, r.compile().parseGetResultOrThrow("242343"))
        Assert.assertEquals("AdsjdsjAAbbbzzz", r.compile().parseGetResultOrThrow("AdsjdsjAAbbbzzz"))
    }
}