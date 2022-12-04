package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.Rules.char
import kotlin.test.Test
import kotlin.test.assertEquals

class AnyCharRuleTest {
    @Test
    fun parse() {
        assertEquals(char.compile().parseOrThrow("a"), 1)
    }

    @Test
    fun parse2() {
        assertEquals(char.compile().parseOrThrow("awerr"), 1)
    }

    @Test
    fun parseWithResult() {
        val result = char.compile().parseWithResult("a")
        assertEquals(result.data, 'a')
        assertEquals(result.endSeek, 1)
    }

    @Test
    fun parseWithResult2() {
        val result = char.compile().parseWithResult("adsdsds")
        assertEquals(result.data, 'a')
        assertEquals(result.endSeek, 1)
    }

    @Test
    fun parseEof() {
        val result = char.compile().parseWithResult("")
        assertEquals(result.errorCode, ParseCode.EOF)
        assertEquals(result.endSeek, 0)

        assertEquals(char.compile().tryParse(""), null)
    }

    @Test
    fun noTest() {
        val result = (!char).compile().parseWithResult("2")
        assertEquals(result.errorCode, ParseCode.NO_FAILED)
        assertEquals(result.endSeek, 0)
    }

    @Test
    fun noTestEof() {
        val result = (!char).compile().parseWithResult("")
        assertEquals(result.isError, false)
        assertEquals(result.endSeek, 0)
    }
}