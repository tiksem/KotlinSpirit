package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.digit
import com.kotlinspirit.core.Rules.nonEmptyLatinStr
import com.kotlinspirit.core.Rules.uint
import org.junit.Assert
import org.junit.Test

class ResultSequenceRuleTest {
    @Test
    fun after1() {
        val sqlArgRule = char(':') + nonEmptyLatinStr.asResult()
        val parser = sqlArgRule.compile()
        Assert.assertEquals(parser.parseGetResultOrThrow(":userId"), "userId")
    }

    @Test
    fun after2() {
        val argNumberRule = char(':') + uint.asResult()
        val parser = argNumberRule.compile()
        Assert.assertEquals(parser.parseGetResultOrThrow(":345"), 345.toUInt())
    }

    @Test
    fun before1() {
        val sqlArgRule = nonEmptyLatinStr.asResult() + char(':')
        val parser = sqlArgRule.compile()
        Assert.assertEquals(parser.parseGetResultOrThrow("userId:"), "userId")
    }

    @Test
    fun before2() {
        val argNumberRule = uint.asResult() + char(':')
        val parser = argNumberRule.compile()
        Assert.assertEquals(parser.parseGetResultOrThrow("345:"), 345.toUInt())
    }

    @Test
    fun between1() {
        val sqlArgRule = char(':') + nonEmptyLatinStr.asResult() + char(':')
        val parser = sqlArgRule.compile()
        Assert.assertEquals(parser.parseGetResultOrThrow(":userId:"), "userId")
    }

    @Test
    fun between2() {
        val argNumberRule = char(':') + uint.asResult() + char(':')
        val parser = argNumberRule.compile()
        Assert.assertEquals(parser.parseGetResultOrThrow(":345:"), 345.toUInt())
    }

    @Test
    fun extra1() {
        val sqlArgRule = char(':') + nonEmptyLatinStr.asResult() + char(':') + digit % ','
        val parser = sqlArgRule.compile()
        Assert.assertEquals(parser.parseGetResultOrThrow(":userId:3,4"), "userId")
    }

    @Test
    fun extra2() {
        val argNumberRule = digit % ',' + char(':') + uint.asResult() + char(':')
        val parser = argNumberRule.compile()
        Assert.assertEquals(parser.parseGetResultOrThrow("3,4,5,1:345:"), 345.toUInt())
    }
}