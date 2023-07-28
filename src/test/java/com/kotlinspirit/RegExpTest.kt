package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.core.Rules.regexp
import org.junit.Assert
import org.junit.Test

class RegExpTest {
    private val emailPattern = Regex(
        "([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z]+)"
    )

    private val r = regexp(emailPattern) + int
    private val p = r.compile()

    @Test
    fun testMatch() {
        p.matchOrThrow("yo@gmail.com123")
    }

    @Test
    fun testNoMatch() {
        Assert.assertEquals(p.parse("yogmail.com123").parseCode, ParseCode.REGEX_NO_MATCH)
    }

    @Test
    fun testFailIf() {
        val r = regexp(emailPattern).failIf {
            it.value != "yo@gmail.com"
        } + int
        val p = r.compile()
        p.matchOrThrow("yo@gmail.com123")
    }
}