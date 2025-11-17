package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.int
import org.junit.Assert
import org.junit.Test

class ReverseIntTest {
    @Test
    fun nameHavingNumberBeforeIt() {
        val name = char('A'..'Z') + +char('a'..'z')
        val r = name.requiresPrefix(int)
        val p = r.compile()
        Assert.assertEquals(
            "Albert",
            p.findFirst("232332Albert")?.toString()
        )
        Assert.assertEquals(
            null,
            p.findFirst("+---Albert")?.toString()
        )
        Assert.assertEquals(
            null,
            p.findFirst("+Albert")?.toString()
        )
        Assert.assertEquals(
            null,
            p.findFirst("-Albert")?.toString()
        )
        Assert.assertEquals(
            "Albert",
            p.findFirst("-4434Albert")?.toString()
        )
        Assert.assertEquals(
            null,
            p.findFirst("${Int.MAX_VALUE.toLong() + 1}Albert")?.toString()
        )
        Long.MAX_VALUE
        Assert.assertEquals(
            "Albert",
            p.findFirst("${Int.MAX_VALUE}Albert")?.toString()
        )
        Assert.assertEquals(
            "Albert",
            p.findFirst("${-Int.MAX_VALUE}Albert")?.toString()
        )
        Assert.assertEquals(
            null,
            p.findFirst("${-Int.MAX_VALUE.toLong() - 1}Albert")?.toString()
        )
        Assert.assertEquals(
            null,
            p.findFirst("Albert")
        )
    }
}