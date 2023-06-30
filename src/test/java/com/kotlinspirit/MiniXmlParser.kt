package com.kotlinspirit

import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.dynamicString
import com.kotlinspirit.core.Rules.dynamicRule
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
        val firstTagNameOccurrence = nonEmptyLatinStr {
            name = it.toString()
        }

        val secondTagNameOccurrence = dynamicString {
            name
        }

        val tagName = dynamicRule {
            if (name.isEmpty()) {
                firstTagNameOccurrence
            } else {
                secondTagNameOccurrence
            }
        }

        val openingTag = char('<') + tagName + char('>')

        val closedTag = str("</") + tagName + '>'

        return openingTag + xmlTagBody {
            body = it
        } + closedTag
    }

    override fun resetResult() {
        name = ""
    }
}.toRule()

private val xmlTagBody: Rule<List<Any>> = (xmlRule or (char - char('<', '>')).repeat()).repeat()

private val parser = xmlRule.compile()
private val xmlReverseParser = str("Abdula").requiresPrefix(xmlRule).compile()

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

    @Test
    fun testHtmlReverseTagParser() {
        Assert.assertEquals(1, xmlReverseParser.count("<a></a>Abdula"))
        Assert.assertEquals(2, xmlReverseParser.count("<a>Hello!</a>Abdula___<a></a>Abdula"))
        val withFailIf = str("Abdula").requiresPrefix(xmlRule.failIf {
            it != Xml(body = listOf("Hello!"), name = "a")
        }).compile()
        Assert.assertEquals(1, withFailIf.count("<a>Hello!</a>Abdula_______"))

        val withFailIf2 = str("Abdula").requiresPrefix(xmlRule.failIf {
            it != Xml(body = listOf(
                Xml(body = listOf("yo"), name = "b"),
                "Hello!",
                Xml(body = listOf(), name = "c"),
            ), name = "a")
        }).compile()
        Assert.assertEquals(1, withFailIf2.count("<a><b>yo</b>Hello!<c></c></a>Abdula"))
    }
}