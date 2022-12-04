package com.kotlinspirit

import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rules.oneOf
import com.kotlinspirit.core.Rules.str
import org.junit.Assert
import org.junit.Test

class OneOfRuleTest {
    @Test
    fun test1() {
        var result: ParseResult<CharSequence>
        val rule = oneOf("Igor", "Vasia", "Petia", "Igorian").compile()
        result = rule.parseWithResult("Igor")
        assertEquals(result.endSeek, "Igor".length)
        assertEquals(result.data!!.toString(), "Igor")

        result = rule.parseWithResult("Vasia")
        assertEquals(result.endSeek, "Vasia".length)
        assertEquals(result.data!!.toString(), "Vasia")

        result = rule.parseWithResult("Petia")
        assertEquals(result.endSeek, "Petia".length)
        assertEquals(result.data!!.toString(), "Petia")

        result = rule.parseWithResult("Igorian")
        assertEquals(result.endSeek, "Igorian".length)
        assertEquals(result.data!!.toString(), "Igorian")

        result = rule.parseWithResult("Igoruuuu")
        assertEquals(result.endSeek, "Igor".length)
        assertEquals(result.data!!.toString(), "Igor")

        result = rule.parseWithResult("Igoria")
        assertEquals(result.endSeek, "Igor".length)
        assertEquals(result.data!!.toString(), "Igor")
    }

    @Test
    fun test2() {
        var result: ParseResult<CharSequence>
        val rule = (str("Igor") or "Vasia" or "Petia" or "Igorian").compile()
        result = rule.parseWithResult("Igor")
        assertEquals(result.endSeek, "Igor".length)
        assertEquals(result.data!!.toString(), "Igor")

        result = rule.parseWithResult("Vasia")
        assertEquals(result.endSeek, "Vasia".length)
        assertEquals(result.data!!.toString(), "Vasia")

        result = rule.parseWithResult("Petia")
        assertEquals(result.endSeek, "Petia".length)
        assertEquals(result.data!!.toString(), "Petia")

        result = rule.parseWithResult("Igorian")
        assertEquals(result.endSeek, "Igorian".length)
        assertEquals(result.data!!.toString(), "Igorian")

        result = rule.parseWithResult("Igoruuuu")
        assertEquals(result.endSeek, "Igor".length)
        assertEquals(result.data!!.toString(), "Igor")

        result = rule.parseWithResult("Igoria")
        assertEquals(result.endSeek, "Igor".length)
        assertEquals(result.data!!.toString(), "Igor")
    }
}