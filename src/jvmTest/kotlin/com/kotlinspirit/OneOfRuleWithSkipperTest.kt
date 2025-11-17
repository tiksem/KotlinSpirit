package com.kotlinspirit

import com.kotlinspirit.core.Rules.caseInsensitiveOneOf
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.oneOf
import com.kotlinspirit.core.Rules.space
import com.kotlinspirit.core.Rules.str
import org.junit.Assert
import org.junit.Test

class OneOfRuleWithSkipperTest {
    private val skipper = (space or char('*',',','-','.','\'','"')).repeat()

    private val parser = oneOf(strings = listOf("simperyy", "glina"), skipper = skipper).compile()
    private val parserCaseInsensitive = caseInsensitiveOneOf(strings = listOf("simperyy", "glina"), skipper = skipper).compile()

    private val parserReverse = str(" telegram").requiresPrefix(oneOf(strings = listOf("simperyy", "glina"), skipper = skipper)).compile()
    private val parserCaseInsensitiveReverse = str(" telegram").requiresPrefix(caseInsensitiveOneOf(strings = listOf("simperyy", "glina"), skipper = skipper)).compile()

    @Test
    fun test1() {
        Assert.assertTrue(parser.matches("sim.peryy"))
        Assert.assertFalse(parser.matches(",,sim.peryy"))
        Assert.assertTrue(parser.matches("sim.   p,,   ery   y"))

        Assert.assertTrue(parserCaseInsensitive.matches("Sim.peryY"))
        Assert.assertFalse(parserCaseInsensitive.matches(",,sim.peryY"))
        Assert.assertTrue(parserCaseInsensitive.matches("sim.   p,,   eRy   y"))

        Assert.assertTrue(parserReverse.findFirst("sim.peryy telegram") != null)
        Assert.assertTrue(parserReverse.findFirst("sim.   p,,   ery   y telegram") != null)

        Assert.assertTrue(parserCaseInsensitiveReverse.findFirst("Sim.peryY telegram") != null)
        Assert.assertTrue(parserCaseInsensitiveReverse.findFirst("sim.   p,,   eRy   y telegram") != null)
    }

    @Test
    fun test2() {
        val text = "Hello goddess! I’m interested in buying some feet pics with worn panties from you, I’m also wiling to pay to worship your ass xoxo. Please hit my  or sc \uD83D\uDC7B\uD83D\uDC7B\uD83D\uDC7B\uD83D\uDE0D\n" +
                "sim.peryy some other text"
        Assert.assertEquals("Hello goddess! I’m interested in buying some feet pics with worn panties from you, I’m also wiling to pay to worship your ass xoxo. Please hit my  or sc \uD83D\uDC7B\uD83D\uDC7B\uD83D\uDC7B\uD83D\uDE0D\n" +
                "ueee some other text", parser.replaceAll(text, "ueee").toString())
    }

    @Test
    fun test3() {
        val text = "Hello goddess! I’m interested in buying some feet pics with worn panties from you, I’m also wiling to pay to worship your ass xoxo. Please hit my  or sc \uD83D\uDC7B\uD83D\uDC7B\uD83D\uDC7B\uD83D\uDE0D\n" +
                "sim.peryY some other text"
        Assert.assertEquals("Hello goddess! I’m interested in buying some feet pics with worn panties from you, I’m also wiling to pay to worship your ass xoxo. Please hit my  or sc \uD83D\uDC7B\uD83D\uDC7B\uD83D\uDC7B\uD83D\uDE0D\n" +
                "ueee some other text", parserCaseInsensitive.replaceAll(text, "ueee").toString())
    }
}