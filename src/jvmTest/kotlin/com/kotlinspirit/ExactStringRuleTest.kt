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
        Assert.assertEquals(r.compile().tryParse(""), 0)
    }

    @Test
    fun noTestIgnoreCase() {
        val r = !str("some", ignoreCase = true)
        Assert.assertEquals(r.compile().tryParse(""), 0)
    }

    @Test
    fun emptyIgnoreCase() {
        Assert.assertEquals(str("", ignoreCase = true).compile().parseGetResultOrThrow(""), "")
    }

    @Test
    fun someIgnoreCase() {
        Assert.assertEquals(str("somE", ignoreCase = true).compile().parseGetResultOrThrow("SoME"), "SoME")
    }

    @Test
    fun someRepeatIgnoreCase() {
        Assert.assertArrayEquals(
            str("Some", ignoreCase = true).repeat().compile().parseGetResultOrThrow("somEsomESome").toTypedArray(),
            arrayOf<CharSequence>("somE", "somE", "Some")
        )
    }
}