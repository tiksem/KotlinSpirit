package com.kotlinspirit

import com.kotlinspirit.core.Rules.moveAfterFirstMatchOf
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.ext.DuplicateRemovalPolicy
import com.kotlinspirit.ext.findFirst
import com.kotlinspirit.ext.removeDuplicates
import org.junit.Assert
import org.junit.Test

class RemoveDuplicatesAndAfterFirstTest {
    @Test
    fun test() {
        val string = "Hey dude 12323, I am good 3422323, and you?"
        Assert.assertEquals(
            string.removeDuplicates(int, DuplicateRemovalPolicy.KEEP_FIRST).toString(),
            "Hey dude 12323, I am good , and you?"
        )
        Assert.assertEquals(
            string.removeDuplicates(int, DuplicateRemovalPolicy.KEEP_LAST).toString(),
            "Hey dude , I am good 3422323, and you?"
        )

        val r = char.repeat().withPrefix(moveAfterFirstMatchOf(int) + ", ")
        Assert.assertEquals(string.findFirst(r), "I am good 3422323, and you?")
    }
}