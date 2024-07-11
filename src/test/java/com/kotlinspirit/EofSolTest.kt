package com.kotlinspirit

import com.kotlinspirit.core.Rules
import com.kotlinspirit.core.Rules.end
import com.kotlinspirit.core.Rules.start
import com.kotlinspirit.core.Rules.space
import org.junit.Test

class EofSolTest {
    @Test
    fun test1() {
        val rule = Rules.caseInsensitiveOneOf("revolut").quoted(space or start, end or space)
        val parser = rule.compile()
        Asserts.listStringEquals(parser.findAll(" Revolut:"), emptyList<String>())
        Asserts.listStringEquals(parser.findAll(" Revolut "), listOf("Revolut"))
        Asserts.listStringEquals(parser.findAll(" Revolut"), listOf("Revolut"))
        Asserts.listStringEquals(parser.findAll("Revolut"), listOf("Revolut"))
        Asserts.listStringEquals(parser.findAll("-Revolut "), emptyList<String>())
    }
}