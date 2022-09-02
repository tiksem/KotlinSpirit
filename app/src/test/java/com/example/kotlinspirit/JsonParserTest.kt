package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.double
import com.example.kotlinspirit.Rules.str
import com.example.kotlinspirit.Rules.lazy
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

private val jsonString = object : Grammar<CharSequence>() {
    override var result: CharSequence = ""
        private set

    override fun defineRule(): Rule<*> {
        val ch = !char('"') or str("\\\"")
        return char('"') + (ch.repeat().asStringRule()) {
            result = it
        } + char('"')
    }
}

private val skipper = str {
    it.isWhitespace()
}

private val value: LazyRule<Any> = lazy {
    jsonString or double or jsonObject or jsonArray
}

private val jsonObject = object : Grammar<Map<String, Any>>() {
    var key: CharSequence = ""
    override var result = LinkedHashMap<String, Any>()
        private set

    override fun resetResult() {
        result = LinkedHashMap()
    }

    override fun defineRule(): Rule<*> {
        val jsonPair = skipper + jsonString {
            key = it
        } + skipper + ':' + skipper + value {
            result[key.toString()] = it
        } + skipper
        return char('{') + skipper + jsonPair.split(
            divider = ',',
            range = 0..Int.MAX_VALUE
        ) + '}'
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
        } + skipper).split(',', 0..Int.MAX_VALUE) + ']'
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

private fun Map<out Any, Any>.contentEquals(other: Map<out Any, Any>): Boolean {
    if (size != other.size) {
        return false
    }

    if (!this.keys.toTypedArray().contentEquals(other.keys.toTypedArray())) {
        return false
    }

    fun anyEqual(v: Any, v2: Any): Boolean {
        if (v == v2) {
            return true
        }

        when (v) {
            is Map<*, *> -> {
                return (v as Map<String, Any>).contentEquals(
                    v2 as? Map<String, Any> ?: return false
                )
            }
            is List<*> -> {
                val v2List = v2 as? List<*> ?: return false
                if (v.size != v2List.size) {
                    return false
                }

                v.zip(v2List).forEach {
                    if (!anyEqual(it.first ?: return false, it.second ?: return false)) {
                        return false
                    }
                }
            }
        }

        return true
    }

    keys.forEach {
        val v = this[it]!!
        val v2 = other[it]!!

        if (!anyEqual(v, v2)) {
            return false
        }
    }

    return true
}

class JsonParserTest {
    @Test
    fun stringTest() {
        Assert.assertEquals(
            "some str",
            jsonString.compile().parseGetResultOrThrow("\"some str\"")
        )
    }

    @Test
    fun valueStringTest() {
        Assert.assertEquals(
            "some str",
            value.compile().parseGetResultOrThrow("\"some str\"")
        )
    }

    @Test
    fun testArray() {
        Assert.assertArrayEquals(
            arrayOf(1223233.0, "aaaaaa", 123456.0),
            jsonArray.compile().parseGetResultOrThrow("[  1223233, \"aaaaaa\", 123456]").toTypedArray()
        )
    }

    @Test
    fun emptyArrayTest() {
        val scanner = Scanner("")
        Assert.assertArrayEquals(
            arrayOf(),
            jsonArray.compile().parseGetResultOrThrow("[]").toTypedArray()
        )
    }

    @Test
    fun testObject() {
        val res = jsonObject.compile().parseGetResultOrThrow(
            "{\"str\": \n\n\n\"value\", \"int\": 1234345, \"arr\": [123, \"123\", \n\n12]   }"
        )

        res.contentEquals(
            mapOf(
                "str" to "value",
                "int" to 1234345,
                "arr" to listOf(123, "123", 12)
            )
        )
    }

    @Test
    fun exampleJson() {
        val str = "{\n" +
                "  \"data\": [{\n" +
                "    \"type\": \"articles\",\n" +
                "    \"id\": \"1\",\n" +
                "    \"attributes\": {\n" +
                "      \"title\": \"JSON:API paints my bikeshed!\",\n" +
                "      \"body\": \"The shortest article. Ever.\",\n" +
                "      \"created\": \"2015-05-22T14:56:29.000Z\",\n" +
                "      \"updated\": \"2015-05-22T14:56:28.000Z\"\n" +
                "    },\n" +
                "    \"relationships\": {\n" +
                "      \"author\": {\n" +
                "        \"data\": {\"id\": \"42\", \"type\": \"people\"}\n" +
                "      }\n" +
                "    }\n" +
                "  }],\n" +
                "  \"included\": [\n" +
                "    {\n" +
                "      \"type\": \"people\",\n" +
                "      \"id\": \"42\",\n" +
                "      \"attributes\": {\n" +
                "        \"name\": \"John\",\n" +
                "        \"age\": 80,\n" +
                "        \"gender\": \"male\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}"
        val value = jsonObject.compile().parseGetResultOrThrow(str)
        JSONAssert.assertEquals(JSONObject(str), JSONObject(value), true)
    }
}