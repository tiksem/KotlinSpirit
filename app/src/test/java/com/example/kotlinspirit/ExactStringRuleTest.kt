package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.str
import org.junit.Assert
import org.junit.Test

class ExactStringRuleTest {
    @Test
    fun empty() {
        Assert.assertEquals(str("").parseOrThrow(""), "")
    }

    @Test
    fun some() {
        Assert.assertEquals(str("some").parseOrThrow("some"), "some")
    }

    @Test
    fun someRepeat() {
        Assert.assertArrayEquals(
            str("some").repeat().parseOrThrow("somesomesome").toTypedArray(),
            arrayOf<CharSequence>("some", "some", "some")
        )
    }
}