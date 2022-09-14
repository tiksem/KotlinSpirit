package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.charIf
import com.example.kotlinspirit.Rules.digit
import com.example.kotlinspirit.Rules.double
import com.example.kotlinspirit.Rules.int
import com.example.kotlinspirit.Rules.space
import com.example.kotlinspirit.Rules.str
import org.junit.Assert
import org.junit.Test

class DiffRuleTest {
    private fun <T : Any> verifyDiffFailed(rule: Rule<T>, str: String, code: Int = ParseCode.DIFF_FAILED) {
        val result = ParseResult<T>()
        rule.parseWithResult(0, str, result)
        Assert.assertEquals(result.parseResult.getParseCode(), code)
        Assert.assertEquals(result.seek, 0)
    }

    private fun <T : Any> verifyDiffSuccess(rule: Rule<T>, str: String) {
        Assert.assertEquals(rule.compile().parseOrThrow(str), str.length)
    }

    @Test
    fun comment() {
        val rule = str("/*") + (char - "*/").repeat() + "*/"
        verifyDiffSuccess(rule, "/*Hello world*/")
    }

    @Test
    fun intButNo2() {
        val rule = int - '2'
        verifyDiffSuccess(rule, "343334")
        verifyDiffFailed(rule, "22323")
        verifyDiffFailed(rule, "2")
    }

    @Test
    fun intButNo232() {
        val rule = int - "232"
        verifyDiffFailed(rule, "2324343")
        verifyDiffFailed(rule, "232")
        verifyDiffSuccess(rule, "3232")
    }

    @Test
    fun wordsButNot2OreMoreNumbers() {
        val main = +(char - space) % space
        val diff = ('0'.expect(!digit) or (char('1'..'9') + digit.repeat()))
            .split(' ',2..Int.MAX_VALUE)
        val rule = +(main - diff)

        verifyDiffSuccess(rule, "Hello my dear friend")
        verifyDiffSuccess(rule, "233444")
        verifyDiffSuccess(rule, "233444 034434343")
        verifyDiffSuccess(rule, "034434343 322323")
        verifyDiffSuccess(rule, "034434343")
        verifyDiffFailed(rule, "433443 44444")
        verifyDiffFailed(rule, "433443 44444 43433434")
        verifyDiffFailed(rule, "", ParseCode.EOF)
    }
}