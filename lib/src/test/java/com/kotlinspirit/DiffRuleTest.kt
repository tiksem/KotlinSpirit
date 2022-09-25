package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.digit
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.core.Rules.space
import com.kotlinspirit.core.Rules.str
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

    @Test
    fun noTestWordButNot() {
        val word = char('A'..'Z') + +char('a'..'z')
        val r = !(word - "Hello")
        Assert.assertEquals(r.compile().tryParse("Hi Yo Hello"), null)
        Assert.assertEquals(r.compile().tryParse("Hello world!"), "Hello world!".length)
        Assert.assertEquals(r.compile().tryParse("Hello World!"), "Hello ".length)
        Assert.assertEquals(r.compile().tryParse("Hi"), null)
        Assert.assertEquals(r.compile().tryParse("345634Hello"), "345634Hello".length)
        Assert.assertEquals(r.compile().tryParse(" Hello 3445 Hi"), " Hello 3445 ".length)
    }

    @Test
    fun isNotComment() {
        val comment = str("/*") + (char - "*/").repeat() + str("*/")
        val notComment = +(char - comment)
        val p = notComment.compile()
        Assert.assertEquals(p.tryParse("12345"), "12345".length)
        Assert.assertEquals(p.tryParse("12345/*"), "12345/*".length)
        Assert.assertEquals(p.tryParse("/*"), "/*".length)
        Assert.assertEquals(p.tryParse("/*edssdds*"), "/*edssdds*".length)
        Assert.assertEquals(p.tryParse("abcd/*edssdds*/**/"), "abcd".length)
        Assert.assertEquals(p.tryParse("abcd/edssdds**4/**/"), "abcd*edssdds**4".length)
        Assert.assertEquals(p.tryParse("/*3434*/"), null)
        Assert.assertEquals(p.tryParse("/**/"), null)
    }
}