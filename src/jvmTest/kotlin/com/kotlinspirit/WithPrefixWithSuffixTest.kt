package com.kotlinspirit

import com.kotlinspirit.core.Rules.nonEmptyLatinStr
import com.kotlinspirit.core.Rules.uint
import com.kotlinspirit.ext.parseWhole
import org.junit.Assert
import org.junit.Test

class WithPrefixWithSuffixTest {
    @Test
    fun testSuffix() {
        val uIntRule = uint.withSuffix("u")
        Assert.assertEquals("2345u".parseWhole(uIntRule), 2345u)
        Assert.assertEquals("2345".parseWhole(uIntRule), null)
        Assert.assertEquals("e2345ue".parseWhole(uIntRule.quoted('e')), 2345u)
    }

    @Test
    fun testPrefix() {
        val r = nonEmptyLatinStr.withPrefix("pre")
        Assert.assertEquals("predefined".parseWhole(r), "defined")
        Assert.assertEquals("pre".parseWhole(r), null)
        Assert.assertEquals("*predefined*".parseWhole(r.quoted('*')), "defined")
    }
}