package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.digit
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.core.expectsSuffix
import org.junit.Assert
import org.junit.Test

class SuffixExpectationRuleTest {
    @Test
    fun number() {
        val rule = '0'.expectsSuffix(!digit) or (char('1'..'9') + digit.repeat())
        val parser = rule.compile()
        parser.matchOrThrow("0")
        parser.matchOrThrow("43434334")
        Assert.assertEquals(parser.matches("0333"), false)
        Assert.assertEquals(parser.matches(" "), false)
        Assert.assertEquals(parser.matches("dsds"), false)
    }

    @Test
    fun intButNotDouble() {
        val rule = int.expectsSuffix(!char('.'))
        val parser = rule.compile()
        parser.matchOrThrow("0")
        parser.matchOrThrow("43434334")
        Assert.assertEquals(parser.matches("2.56"), false)
        Assert.assertEquals(parser.matches("eee"), false)
    }
}