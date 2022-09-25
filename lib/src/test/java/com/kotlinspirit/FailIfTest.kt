package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.Rules.int
import org.junit.Assert
import org.junit.Test

class FailIfTest {
    @Test
    fun test1() {
        val r = int.failIf {
            it > 500
        }.compile()

        Assert.assertEquals(r.parse("1235").errorCode, ParseCode.FAIL_PREDICATE)
        Assert.assertEquals(r.parse("123").isError, false)
        Assert.assertEquals(r.parse("").errorCode, ParseCode.EOF)
    }

    @Test
    fun noTest() {
        val r = (!(int.failIf {
            it > 500
        })).compile()

        Assert.assertEquals(r.parse("1235").isError, false)
        Assert.assertEquals(r.parse("123").errorCode, ParseCode.NO_FAILED)
        Assert.assertEquals(r.parse("").isError, false)
        Assert.assertEquals(r.tryParse("1235dsdds1235"), 1)
        Assert.assertEquals(r.tryParse("1235dsdds1235s4"), 1)
    }
}