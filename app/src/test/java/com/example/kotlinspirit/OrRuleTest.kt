package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.int
import com.example.kotlinspirit.Rules.latinStr
import org.junit.Assert
import org.junit.Test

class OrRuleTest {
    @Test
    fun intOrString() {
        val r = int or latinStr
        Assert.assertEquals(242343, r.parseWithResultOrThrow("242343"))
        Assert.assertEquals("AdsjdsjAAbbbzzz", r.parseWithResultOrThrow("AdsjdsjAAbbbzzz"))
    }
}