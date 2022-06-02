package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.int
import com.example.kotlinspirit.Rules.latinStr
import com.example.kotlinspirit.Rules.spaceStr
import org.junit.Assert
import org.junit.Test

class SequenceRuleTest {
    @Test
    fun intBetweenChars() {
        var e = 0
        val r = char('[') + int.on {
            e = it
        } + '*'
        r.parseOrThrow("[343443*")
        Assert.assertEquals(343443, e)
    }

    @Test
    fun intBetweenCharsWithSkipper() {
        var e = 0
        val r = char('[') + int.on {
            e = it
        } + '*'
        r.parseOrThrow("      [      \n343443       *       ", skipper = spaceStr)
        Assert.assertEquals(343443, e)
    }

    @Test
    fun intOrStringBetweenChars() {
        var e = 0
        var str: CharSequence = ""
        val r = char('[') + (int.on {
            e = it
        } or latinStr.on {
            str = it
        }) + '*'
        r.parseOrThrow("[343443*")
        Assert.assertEquals(343443, e)
        r.parseOrThrow("[abcd*")
        Assert.assertEquals("abcd", str)
    }

    @Test
    fun intOrStringBetweenCharsWithSkipper() {
        var e = 0
        var str: CharSequence = ""
        val r = char('[') + (int.on {
            e = it
        } or latinStr.on {
            str = it
        }) + '*'
        r.parseOrThrow("    [      343443   *    ", skipper = spaceStr)
        Assert.assertEquals(343443, e)
        r.parseOrThrow("    [      \n\n\nabcd    *    ", skipper = spaceStr)
        Assert.assertEquals("abcd", str)
    }
}