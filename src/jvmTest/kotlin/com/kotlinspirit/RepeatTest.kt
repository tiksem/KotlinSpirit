package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.digit
import com.kotlinspirit.core.expectsSuffix
import org.junit.Assert
import org.junit.Test

class RepeatTest {
    @Test
    fun numbers() {
        val number = '0'.expectsSuffix(!digit) or (char('1'..'9') + digit.repeat())
        val numbers = number.split(' ', 2..Int.MAX_VALUE)
        val parser = numbers.compile()
        Assert.assertEquals(parser.matches("233444 034434343"), false)
    }
}