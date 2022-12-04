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
        com.kotlinspirit.Asserts.listStringEquals(
            p.parseGetResultOrThrow("HelloWorld"),
            listOf("Hello", "World")
        )
        com.kotlinspirit.Asserts.listStringEquals(
            p.parseGetResultOrThrow("Helloworld"),
            listOf("Helloworld")
        )
        assertEquals(p.tryParse(""), null)
        assertEquals(
            p.parse("aHelloworld").errorCode,
            ParseCode.CHAR_PREDICATE_FAILED
        )
        val r = p.parseWithResult("HelloWorld123Ebanko")
        com.kotlinspirit.Asserts.listStringEquals(
            r.data!!,
            listOf("Hello", "World")
        )
        assertEquals(r.endSeek, "HelloWorld".length)
        assertEquals(r.isError, false)
    }

    @Test
    fun groupOfNames() {
        val name = oneOf("Julia", "Peter", "Clown", "Dog", "Turbo", "Somer")
        val names = +name
        val p = names.compile()
        com.kotlinspirit.Asserts.listStringEquals(
            p.parseGetResultOrThrow("JuliaClown"),
            listOf("Julia", "Clown")
        )
        com.kotlinspirit.Asserts.listStringEquals(
            p.parseGetResultOrThrow("JuliaPeterClownDogTurboSomer"),
            listOf("Julia", "Peter", "Clown", "Dog", "Turbo", "Somer")
        )
        com.kotlinspirit.Asserts.listStringEquals(
            p.parseGetResultOrThrow("JuliaCl"),
            listOf("Julia")
        )
        com.kotlinspirit.Asserts.listStringEquals(
            p.parseGetResultOrThrow("SomerJul"),
            listOf("Somer")
        )
        assertEquals(p.tryParse(""), null)
    }
}