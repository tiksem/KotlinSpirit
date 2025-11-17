package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.ext.parseWholeOrThrow
import org.junit.Assert
import org.junit.Test

class ParseWholeOrThrowTest {
    @Test
    fun test() {
        val r = int.withSuffix(char.repeat())
        Assert.assertEquals("3443443sdffdsdsf".parseWholeOrThrow(r), 3443443)
    }
}