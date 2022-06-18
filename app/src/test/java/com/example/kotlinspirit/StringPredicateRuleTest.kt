package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import org.junit.Test

class StringPredicateRuleTest {
    @Test
    fun test1() {
        (char('!', '?') + ' ').matchOrThrow("! ")
    }

    @Test
    fun test2() {
        (char('!', '?') + ' ').matchOrThrow("? ")
    }
}