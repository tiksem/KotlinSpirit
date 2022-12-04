package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.Rules.int
import kotlin.test.Test
import kotlin.test.assertEquals

class FailIfTest {
    @Test
    fun test1() {
        val r = int.failIf {
            it > 500
        }.compile()

        assertEquals(r.parse("1235").errorCode, ParseCode.FAIL_PREDICATE)
        assertEquals(r.parse("123").isError, false)
        assertEquals(r.parse("").errorCode, ParseCode.EOF)
    }

    @Test
    fun noTest() {
        val r = (!(int.failIf {
            it > 500
        })).compile()

        assertEquals(r.parse("1235").isError, false)
        assertEquals(r.parse("123").errorCode, ParseCode.NO_FAILED)
        assertEquals(r.parse("").isError, false)
        assertEquals(r.tryParse("1235dsdds1235"), 1)
        assertEquals(r.tryParse("1235dsdds1235s4"), 1)
    }
}