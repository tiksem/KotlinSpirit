package com.kotlinspirit

import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.dynamicString
import com.kotlinspirit.core.Rules.nonEmptyLatinStr
import com.kotlinspirit.grammar.Grammar
import org.junit.Assert
import org.junit.Test

private data class Tag(
    val body: String,
    val name: String
)

private val parser = object : Grammar<Tag>() {
    private var name = ""
    private var body = ""

    override val result: Tag
        get() = Tag(body, name)

    override fun defineRule(): Rule<*> {
        return char('<') + (nonEmptyLatinStr {
            name = it.toString()
        }) + char('>') + ((char - '<').repeat()) {
            body = it.toString()
        } + "</" + dynamicString {
            name
        } + '>'
    }
}.toRule().compile()

class SimpleHtmlTagParserText {
    @Test
    fun testHtmlTagParser() {
        parser.matchOrThrow("<a></a>")
        parser.matchOrThrow("<a>Hello!</a>")
        Assert.assertFalse(parser.matches("<a>Hello!</b>"))
        Assert.assertFalse(parser.matches("<b>Hello!</a>"))
        Assert.assertEquals(parser.parseGetResultOrThrow("<a>Hello!</a>"), Tag(body = "Hello!", name = "a"))
    }
}