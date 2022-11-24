package com.kotlinspirit

import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.double
import com.kotlinspirit.core.Rules.str
import com.kotlinspirit.core.Rules.lazy
import com.kotlinspirit.expressive.LazyRule
import com.kotlinspirit.grammar.Grammar
import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.util.*
import kotlin.collections.ArrayList

private val jsonString = (str("\\\"") or (char - '"')).repeat().asString().quoted('"')

private val skipper = str {
    it.isWhitespace()
}

private val value: LazyRule<Any> = lazy {
    (jsonString or double or jsonObject or jsonArray) as RuleWithDefaultRepeat<Any>
}

private val jsonObject = object : Grammar<Map<String, Any>>() {
    private var key: String = ""
    override var result = LinkedHashMap<String, Any>()
        private set

    override fun resetResult() {
        result = LinkedHashMap()
    }

    override fun defineRule(): Rule<*> {
        val jsonPair = skipper + jsonString {
            key = it.toString().replace("\\\"", "\"")
        } + skipper + ':' + skipper + value {
            result[key] = it
        } + skipper
        return char('{') + skipper + jsonPair.split(
            divider = ',',
            range = 0..Int.MAX_VALUE
        ) + '}'
    }
}.toRule()

private val jsonArray = value.split(char(',').quoted(skipper), 0..Int.MAX_VALUE)
    .quoted('[' + skipper, skipper + ']')

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

        Assert.assertEquals(
            "some \\\" str",
            jsonString.compile().parseGetResultOrThrow("\"some \\\" str\"  ")
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
        val p = jsonObject.compile()
        var time = System.currentTimeMillis()
        repeat(1000) {
            JSONObject(str)
        }
        System.out.println(System.currentTimeMillis() - time)
        time = System.currentTimeMillis()
        repeat(999) {
            p.parseGetResultOrThrow(str)
        }
        val value = p.parseGetResultOrThrow(str)
        System.out.println(System.currentTimeMillis() - time)
        JSONAssert.assertEquals(JSONObject(str), JSONObject(value), true)
    }
}