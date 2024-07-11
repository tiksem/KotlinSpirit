package com.kotlinspirit

import com.kotlinspirit.core.Rules.end
import com.kotlinspirit.core.Rules.int
import org.junit.Assert
import org.junit.Test

class EofTest {
    @Test
    fun testInteger() {
        val r = int + end
        val p = r.compile()
        Assert.assertEquals(p.matches("4343344334"), false) // out of bounds
        Assert.assertEquals(p.matches("434334433"), true)
        Assert.assertEquals(p.matches("4343344334dsdsds"), false)
        Assert.assertEquals(p.indexOf("dd434334433"), 2)
    }

    @Test
    fun testEmptyString() {
        val r = end
        val p = r.compile()
        Assert.assertEquals(p.matches("dsds"), false)
        Assert.assertEquals(p.matches(""), true)
        Assert.assertEquals(p.indexOf(""), 0)
    }
}