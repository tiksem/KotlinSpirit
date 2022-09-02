package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.double
import com.example.kotlinspirit.Rules.int
import com.example.kotlinspirit.Rules.str
import org.junit.Assert
import org.junit.Test

class DiffRuleTest {
    private fun <T : Any> verifyDiffFailed(rule: Rule<T>, str: String) {
        val result = ParseResult<T>()
        rule.parseWithResult(0, str, result)
        Assert.assertEquals(result.parseResult.getParseCode(), ParseCode.DIFF_FAILED)
        Assert.assertEquals(result.seek, 0)
    }

    private fun <T : Any> verifyDiffSuccess(rule: Rule<T>, str: String) {
        Assert.assertEquals(rule.compile().parseOrThrow(str), str.length)
    }

    @Test
    fun doubleButNotInt() {
        val rule = double - int
        verifyDiffFailed(rule, "234")
        verifyDiffSuccess(rule, "234.56")
    }

    @Test
    fun comment() {
        val rule = str("/*") + (char - "*/").repeat() + "*/"
        verifyDiffSuccess(rule, "/*Hello world*/")
    }

    @Test
    fun intButNo2() {
        val rule = int - '2'
        verifyDiffSuccess(rule, "243334")
        verifyDiffFailed(rule, "2")
    }

    @Test
    fun intButNo232() {
        val rule = int - "232"
        verifyDiffSuccess(rule, "2324343")
        verifyDiffFailed(rule, "232")
    }
}