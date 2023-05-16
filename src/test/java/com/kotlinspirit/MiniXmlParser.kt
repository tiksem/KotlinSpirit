package com.kotlinspirit

import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.dynamic
import com.kotlinspirit.core.Rules.nonEmptyLatinStr
import com.kotlinspirit.core.Rules.str
import com.kotlinspirit.grammar.Grammar
import org.junit.Assert
import org.junit.Test

private data class Xml(
    val body: List<Any>,
    val name: String
)

private val xmlRule = object : Grammar<Xml>() {
    private var name = ""
    private var body = emptyList<Any>()

    override val result: Xml
        get() = Xml(body, name)

    override fun defineRule(): Rule<*> {
        val openingTag = char('<') + nonEmptyLatinStr {
            name = it.toString()
        } + char('>')

        val closedTag = str("</") + dynamic {
            name
        } + '>'

        return openingTag + xmlTagBody {
            body = it
        } + closedTag
    }
}.toRule()

private val xmlTagBody: Rule<List<Any>> = (xmlRule or (char - char('<', '>')).repeat()).repeat()

private val parser = xmlRule.compile()

class MiniXmlParser {
    @Test
    fun testHtmlTagParser() {
        parser.matchOrThrow("<a></a>")
        parser.matchOrThrow("<a>Hello!</a>")
        Assert.assertFalse(parser.matches("<a>Hello!</b>"))
        Assert.assertFalse(parser.matches("<b>Hello!</a>"))
        Assert.assertEquals(
            parser.parseGetResultOrThrow("<a>Hello!</a>"),
            Xml(body = listOf("Hello!"), name = "a")
        )

        Assert.assertEquals(
            parser.parseGetResultOrThrow("<a><b>yo</b>Hello!<c></c></a>"),
            Xml(body = listOf(
                Xml(body = listOf("yo"), name = "b"),
                "Hello!",
                Xml(body = listOf(), name = "c"),
            ), name = "a")
        )
    }
}