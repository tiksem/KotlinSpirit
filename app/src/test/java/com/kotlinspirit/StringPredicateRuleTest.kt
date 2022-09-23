package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.str
import org.junit.Assert
import org.junit.Test

class StringPredicateRuleTest {
    @Test
    fun test1() {
        (char('!', '?') + ' ').compile().matchOrThrow("! ")
    }

    @Test
    fun test2() {
        (char('!', '?') + ' ').compile().matchOrThrow("? ")
    }

    @Test
    fun test3() {
        var result: CharSequence = ""
        val rule = str {
            it.isWhitespace()
        } + (str {
            it.isDigit()
        }) {
            result = it
        } + str {
            it.isWhitespace()
        }

        rule.compile().matchOrThrow("   \n\n123\n\n\n\n  ")
        Assert.assertEquals("123", result);
    }
}