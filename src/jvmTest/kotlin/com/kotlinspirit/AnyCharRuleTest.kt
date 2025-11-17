package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rules.char
import org.junit.Assert
import org.junit.Test

class AnyCharRuleTest {
    @Test
    fun parse() {
        Assert.assertEquals(char.compile().parseOrThrow("a"), 1)
        Assert.assertEquals(char.reverseParse(0, "a").seek, -1)
    }

    @Test
    fun parse2() {
        Assert.assertEquals(char.compile().parseOrThrow("awerr"), 1)
        Assert.assertEquals(char.reverseParse(0, "awerr").seek, -1)
    }

    @Test
    fun parseWithResult() {
        val result = char.compile().parseWithResult("a")
        Assert.assertEquals(result.data, 'a')
        Assert.assertEquals(result.endSeek, 1)
    }

    @Test
    fun reverseParseWithResult() {
        val result = ParseResult<Char>()
        char.reverseParseWithResult(0, "a", result)
        Assert.assertEquals(result.data, 'a')
        Assert.assertEquals(result.endSeek, -1)
    }

    @Test
    fun parseWithResult2() {
        val result = char.compile().parseWithResult("adsdsds")
        Assert.assertEquals(result.data, 'a')
        Assert.assertEquals(result.endSeek, 1)
    }

    @Test
    fun reverseParseWithResult2() {
        val result = ParseResult<Char>()
        char.reverseParseWithResult("adsdsds".length - 1, "adsdsds", result)
        Assert.assertEquals(result.data, 's')
        Assert.assertEquals(result.endSeek, "adsdsds".length - 2)
    }

    @Test
    fun parseEof() {
        val result = char.compile().parseWithResult("")
        Assert.assertEquals(result.errorCode, ParseCode.EOF)
        Assert.assertEquals(result.endSeek, 0)

        Assert.assertEquals(char.compile().tryParse(""), null)
    }

    @Test
    fun parseEofReverse() {
        val result = ParseResult<Char>()
        char.reverseParseWithResult(-1, "", result)
        Assert.assertEquals(result.errorCode, ParseCode.EOF)
        Assert.assertEquals(result.endSeek, -1)
    }

    @Test
    fun noTest() {
        val result = (!char).compile().parseWithResult("2")
        Assert.assertEquals(result.errorCode, ParseCode.NO_FAILED)
        Assert.assertEquals(result.endSeek, 0)
    }

    @Test
    fun noTestEof() {
        val result = (!char).compile().parseWithResult("")
        Assert.assertEquals(result.isError, false)
        Assert.assertEquals(result.endSeek, 0)
    }
}