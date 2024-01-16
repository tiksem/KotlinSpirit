package com.kotlinspirit

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rules.caseInsensitiveOneOf
import com.kotlinspirit.core.Rules.oneOf
import org.junit.Assert
import org.junit.Test

class OneOfRuleTest {
    @Test
    fun test1() {
        var result: ParseResult<CharSequence>
        val rule = oneOf("Igor", "Vasia", "Petia", "Igorian").compile()
        result = rule.parseWithResult("Igor")
        Assert.assertEquals(result.endSeek, "Igor".length)
        Assert.assertEquals(result.data!!.toString(), "Igor")

        result = rule.parseWithResult("Vasia")
        Assert.assertEquals(result.endSeek, "Vasia".length)
        Assert.assertEquals(result.data!!.toString(), "Vasia")

        result = rule.parseWithResult("Petia")
        Assert.assertEquals(result.endSeek, "Petia".length)
        Assert.assertEquals(result.data!!.toString(), "Petia")

        result = rule.parseWithResult("Igorian")
        Assert.assertEquals(result.endSeek, "Igorian".length)
        Assert.assertEquals(result.data!!.toString(), "Igorian")

        result = rule.parseWithResult("Igoruuuu")
        Assert.assertEquals(result.endSeek, "Igor".length)
        Assert.assertEquals(result.data!!.toString(), "Igor")

        result = rule.parseWithResult("Igoria")
        Assert.assertEquals(result.endSeek, "Igor".length)
        Assert.assertEquals(result.data!!.toString(), "Igor")
    }

    @Test
    fun test2() {
        var result: ParseResult<CharSequence>
        val rule = oneOf("Igor", "Vasia", "Petia", "Igorian").compile()
        result = rule.parseWithResult("Igor")
        Assert.assertEquals(result.endSeek, "Igor".length)
        Assert.assertEquals(result.data!!.toString(), "Igor")

        result = rule.parseWithResult("Vasia")
        Assert.assertEquals(result.endSeek, "Vasia".length)
        Assert.assertEquals(result.data!!.toString(), "Vasia")

        result = rule.parseWithResult("Petia")
        Assert.assertEquals(result.endSeek, "Petia".length)
        Assert.assertEquals(result.data!!.toString(), "Petia")

        result = rule.parseWithResult("Igorian")
        Assert.assertEquals(result.endSeek, "Igorian".length)
        Assert.assertEquals(result.data!!.toString(), "Igorian")

        result = rule.parseWithResult("Igoruuuu")
        Assert.assertEquals(result.endSeek, "Igor".length)
        Assert.assertEquals(result.data!!.toString(), "Igor")

        result = rule.parseWithResult("Igoria")
        Assert.assertEquals(result.endSeek, "Igor".length)
        Assert.assertEquals(result.data!!.toString(), "Igor")
    }

    @Test
    fun test3() {
        var result: ParseResult<CharSequence>
        val rule = caseInsensitiveOneOf("Igor", "Vasia", "Petia", "Igorian").compile()
        result = rule.parseWithResult("IGOR")
        Assert.assertEquals(result.endSeek, "Igor".length)
        Assert.assertEquals(result.data!!.toString(), "IGOR")

        result = rule.parseWithResult("vasia")
        Assert.assertEquals(result.endSeek, "Vasia".length)
        Assert.assertEquals(result.data!!.toString(), "vasia")

        result = rule.parseWithResult("pETIA")
        Assert.assertEquals(result.endSeek, "Petia".length)
        Assert.assertEquals(result.data!!.toString(), "pETIA")

        result = rule.parseWithResult("igoriaN")
        Assert.assertEquals(result.endSeek, "Igorian".length)
        Assert.assertEquals(result.data!!.toString(), "igoriaN")

        result = rule.parseWithResult("igoRuuuu")
        Assert.assertEquals(result.endSeek, "Igor".length)
        Assert.assertEquals(result.data!!.toString(), "igoR")

        result = rule.parseWithResult("Igoria")
        Assert.assertEquals(result.endSeek, "Igor".length)
        Assert.assertEquals(result.data!!.toString(), "Igor")
    }

    @Test
    fun test4() {
        var result: ParseResult<CharSequence>
        val rule = caseInsensitiveOneOf("Igor", "Vasia", "Petia", "Igorian").compile()
        result = rule.parseWithResult("igor")
        Assert.assertEquals(result.endSeek, "Igor".length)
        Assert.assertEquals(result.data!!.toString(), "igor")

        result = rule.parseWithResult("VasIa")
        Assert.assertEquals(result.endSeek, "Vasia".length)
        Assert.assertEquals(result.data!!.toString(), "VasIa")

        result = rule.parseWithResult("PetiA")
        Assert.assertEquals(result.endSeek, "Petia".length)
        Assert.assertEquals(result.data!!.toString(), "PetiA")

        result = rule.parseWithResult("IgoriaN")
        Assert.assertEquals(result.endSeek, "Igorian".length)
        Assert.assertEquals(result.data!!.toString(), "IgoriaN")

        result = rule.parseWithResult("igoruuuu")
        Assert.assertEquals(result.endSeek, "Igor".length)
        Assert.assertEquals(result.data!!.toString(), "igor")

        result = rule.parseWithResult("igoria")
        Assert.assertEquals(result.endSeek, "Igor".length)
        Assert.assertEquals(result.data!!.toString(), "igor")
    }
}