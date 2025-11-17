package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.oneOf
import org.junit.Assert
import org.junit.Test

class OneOrMoreRuleTest {
    @Test
    fun words() {
        val name = char('A'..'Z') + +(char('a'..'z'))
        val names = +name
        val p = names.compile()
        Asserts.listStringEquals(
            p.parseGetResultOrThrow("HelloWorld"),
            listOf("Hello", "World")
        )
        Asserts.listStringEquals(
            p.parseGetResultOrThrow("Helloworld"),
            listOf("Helloworld")
        )
        Assert.assertEquals(p.tryParse(""), null)
        Assert.assertEquals(
            p.parse("aHelloworld").errorCode,
            ParseCode.CHAR_PREDICATE_FAILED
        )
        val r = p.parseWithResult("HelloWorld123Ebanko")
        Asserts.listStringEquals(
            r.data!!,
            listOf("Hello", "World")
        )
        Assert.assertEquals(r.endSeek, "HelloWorld".length)
        Assert.assertEquals(r.isError, false)
    }

    @Test
    fun groupOfNames() {
        val name = oneOf("Julia", "Peter", "Clown", "Dog", "Turbo", "Somer")
        val names = +name
        val p = names.compile()
        Asserts.listStringEquals(
            p.parseGetResultOrThrow("JuliaClown"),
            listOf("Julia", "Clown")
        )
        Asserts.listStringEquals(
            p.parseGetResultOrThrow("JuliaPeterClownDogTurboSomer"),
            listOf("Julia", "Peter", "Clown", "Dog", "Turbo", "Somer")
        )
        Asserts.listStringEquals(
            p.parseGetResultOrThrow("JuliaCl"),
            listOf("Julia")
        )
        Asserts.listStringEquals(
            p.parseGetResultOrThrow("SomerJul"),
            listOf("Somer")
        )
        Assert.assertEquals(p.tryParse(""), null)
    }
}