package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.digit
import org.junit.Assert
import org.junit.Test

class RepeatTest {
    @Test
    fun numbers() {
        val number = '0'.expect(!digit) or (char('1'..'9') + digit.repeat())
        val numbers = number.split(' ', 2..Int.MAX_VALUE)
        val parser = numbers.compile()
        Assert.assertEquals(parser.matches("233444 034434343"), false)
    }
}