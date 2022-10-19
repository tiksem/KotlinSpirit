package com.kotlinspirit

import com.kotlinspirit.core.Rules.str
import org.junit.Assert
import org.junit.Test

class ExactStringRuleTest {
    @Test
    fun empty() {
        Assert.assertEquals(str("").compile().parseGetResultOrThrow(""), "")
    }

    @Test
    fun some() {
        Assert.assertEquals(str("some").compile().parseGetResultOrThrow("some"), "some")
    }

    @Test
    fun someRepeat() {
        Assert.assertArrayEquals(
            str("some").repeat().compile().parseGetResultOrThrow("somesomesome").toTypedArray(),
            arrayOf<CharSequence>("some", "some", "some")
        )
    }

    @Test
    fun noTest() {
        val r = !str("some")
//        Assert.assertEquals(r.compile().tryParse("some"), null)
//        Assert.assertEquals(r.compile().tryParse("somefddffd"), null)
//        Assert.assertEquals(r.compile().tryParse("dsdsdssome"), 1)
        Assert.assertEquals(r.compile().tryParse(""), 0)
    }
}