package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.digit
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.core.expect
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpectationRuleTest {
    @Test
    fun number() {
        val rule = '0'.expect(!digit) or (char('1'..'9') + digit.repeat())
        val parser = rule.compile()
        parser.matchOrThrow("0")
        parser.matchOrThrow("43434334")
        assertEquals(parser.matches("0333"), false)
        assertEquals(parser.matches(" "), false)
        assertEquals(parser.matches("dsds"), false)
    }

    @Test
    fun intButNotDouble() {
        val rule = int.expect(!char('.'))
        val parser = rule.compile()
        parser.matchOrThrow("0")
        parser.matchOrThrow("43434334")
        assertEquals(parser.matches("2.56"), false)
        assertEquals(parser.matches("eee"), false)
    }
}