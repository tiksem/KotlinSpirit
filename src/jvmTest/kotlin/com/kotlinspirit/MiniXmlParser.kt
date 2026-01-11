package com.kotlinspirit

import com.kotlinspirit.core.Clearable
import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.dynamicString
import com.kotlinspirit.core.Rules.dynamicRule
import com.kotlinspirit.core.Rules.grammar
import com.kotlinspirit.core.Rules.nonEmptyLatinStr
import com.kotlinspirit.core.Rules.str
import org.junit.Assert
import org.junit.Test

private data class Xml(
    val body: List<Any>,
    val name: String
)

private data class XmlData(
    var name: String = "",
    val body: MutableList<Any> = mutableListOf()
) : Clearable {
    override fun clear() {
        body.clear()
    }
}

private val xmlRule = grammar(
    dataFactory = { XmlData() },
    defineRule = { data ->
        val firstTagNameOccurrence = nonEmptyLatinStr {
            data.name = it.toString()
        }

        val secondTagNameOccurrence = dynamicString {
            data.name
        }

        val tagName = dynamicRule {
            if (data.name.isEmpty()) {
                firstTagNameOccurrence
            } else {
                secondTagNameOccurrence
            }
        }

        val openingTag = char('<') + tagName + char('>')

        val closedTag = str("</") + tagName + '>'

        openingTag + xmlTagBody {
            data.body.addAll(it)
        } + closedTag
    },
    getResult = {
        Xml(body = it.body.toList(), name = it.name)
    }
)

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