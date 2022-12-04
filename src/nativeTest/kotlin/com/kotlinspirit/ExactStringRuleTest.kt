package com.kotlinspirit

import com.kotlinspirit.core.Rules.str
import kotlin.test.Test
import kotlin.test.assertEquals

class ExactStringRuleTest {
    @Test
    fun empty() {
        assertEquals(str("").compile().parseGetResultOrThrow(""), "")
    }

    @Test
    fun some() {
        assertEquals(str("some").compile().parseGetResultOrThrow("some"), "some")
    }

    @Test
    fun someRepeat() {
        assertEquals(
            str("some").repeat().compile().parseGetResultOrThrow("somesomesome").toTypedArray(),
            arrayOf<CharSequence>("some", "some", "some")
        )
    }

    @Test
    fun noTest() {
        val r = !str("some")
//        assertEquals(r.compile().tryParse("some"), null)
//        assertEquals(r.compile().tryParse("somefddffd"), null)
//        assertEquals(r.compile().tryParse("dsdsdssome"), 1)
        assertEquals(r.compile().tryParse(""), 0)
    }
}