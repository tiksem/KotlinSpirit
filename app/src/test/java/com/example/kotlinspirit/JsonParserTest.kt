package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.int
import com.example.kotlinspirit.Rules.str
import com.example.kotlinspirit.Rules.lazy
import org.junit.Assert
import org.junit.Test

private val jsonString = object : Grammar<CharSequence>() {
    override var result: CharSequence = ""
        private set

    override fun defineRule(): Rule<*> {
        return char('"') + ((!char('"')).repeat()) {
            result = it
        } + char('"')
    }
}

private val skipper = str {
    it.isWhitespace()
}

private val value: LazyRule<Any> = lazy {
    jsonString or int or jsonObject or jsonArray
}

private val jsonObject = object : Grammar<Map<CharSequence, Any>>() {
    var key: CharSequence = ""
    override var result = LinkedHashMap<CharSequence, Any>()
        private set

    override fun resetResult() {
        result = LinkedHashMap()
    }

    override fun defineRule(): Rule<*> {
        val jsonPair = jsonString {
            key = it
        } + skipper + ':' + skipper + (value {
            result[key] = it
        })
        return char('{') + skipper + jsonPair % ',' + skipper + '}'
    }
}

private val jsonArray = object : Grammar<List<Any>>() {
    override var result = ArrayList<Any>()
        private set

    override fun resetResult() {
        result = ArrayList()
    }

    override fun defineRule(): Rule<*> {
        return char('[') + (skipper + value {
            result.add(it)
        } + skipper) % ',' + ']'
    }
}

private val json = object : Grammar<Any>() {
    override var result: Any = ""
        private set

    override fun defineRule(): Rule<*> {
        return skipper + (jsonObject or jsonArray) {
            result = it
        } + skipper
    }
}

class JsonParserTest {
    @Test
    fun stringTest() {
        Assert.assertEquals(
            "some str",
            jsonString.parseWithResultOrThrow("\"some str\"")
        )
    }

    @Test
    fun valueStringTest() {
        Assert.assertEquals(
            "some str",
            value.parseWithResultOrThrow("\"some str\"")
        )
    }

    @Test
    fun testArray() {
        Assert.assertArrayEquals(
            arrayOf(1223233, "aaaaaa", 123456),
            jsonArray.parseWithResultOrThrow("[  1223233, \"aaaaaa\", 123456]").toTypedArray()
        )
    }
}