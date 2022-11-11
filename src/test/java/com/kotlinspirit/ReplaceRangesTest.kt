package com.kotlinspirit

import com.kotlinspirit.ext.replaceRanges
import org.junit.Assert
import org.junit.Test

class ReplaceRangesTest {
    @Test
    fun testEmpty() {
        Assert.assertEquals("Hello!".replaceRanges(emptyList(), "YO").toString(), "Hello!")
    }

    @Test
    fun testOneRangeMiddle() {
        Assert.assertEquals("Hello!".replaceRanges(listOf(1..4), "YO").toString(), "HYO!")
    }

    @Test
    fun testOneRangeEnd() {
        Assert.assertEquals("Hello!".replaceRanges(listOf(3..5), "YO").toString(), "HelYO")
    }

    @Test
    fun testMix1() {
        Assert.assertEquals("Hello Vasia, How are you today".replaceRanges(
            listOf(0..4, 6..10, 25..29), "YO"
        ).toString(), "YO YO, How are you YO")
    }

    @Test
    fun testConnectedRanges() {
        Assert.assertEquals("Hello Vasia, How are you today".replaceRanges(
            listOf(0..5, 6..10), "YO"
        ).toString(), "YOYO, How are you today")
    }

    @Test
    fun testIntersectionThrow() {
        Assert.assertThrows(IllegalStateException::class.java) {
            "Hello Vasia, How are you today".replaceRanges(
                listOf(0..9, 6..10), "YO"
            )
        }
    }
}