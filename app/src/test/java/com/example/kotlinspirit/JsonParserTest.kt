package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.int
import com.example.kotlinspirit.Rules.quotedString
import com.example.kotlinspirit.Rules.str
import org.junit.Assert
import org.junit.Test

class JsonParserTest {
    val jsonRuleBox = RuleBox<Any>()
    val objRuleBox = RuleBox<Map<CharSequence, Any>>()
    val arrayRuleBox = RuleBox<List<Any>>()
    val valueRuleBox = RuleBox<Any>()
    val pairRuleBox = RuleBox<Pair<CharSequence, Any>>()

    private fun string(): Rule<CharSequence> {
        var stringResult: CharSequence = ""

        return (char('\"') + str {
            it != '\"'
        }.on {
            stringResult = it
        } + '\"').transform {
            stringResult
        }
    }

    val json: Rule<Any> by lazy {
        objRuleBox or arrayRuleBox
    }

    val obj: Rule<Map<CharSequence, Any>> by lazy {
        val result = linkedMapOf<CharSequence, Any>()
        (char('{') + pairRuleBox.on {
            result[it.first] = it.second
        } % ',' + '}').transform {
            result
        }
    }

    val array: Rule<List<Any>> by lazy {
        var result: List<Any>? = null
        (char('[') + (valueRuleBox % ',').on {
            result = it
        } + ']').transform {
            result ?: emptyList()
        }
    }

    val value: Rule<Any> by lazy {
        string() or int
    }

    val jsonPair: Rule<Pair<CharSequence, Any>> by lazy {
        var first: CharSequence = ""
        var second: Any = ""
        (string().on {
            first = it
        } + ": " + valueRuleBox.on {
            second = it
        }).transform {
            Pair(first, second)
        }
    }

    @Test
    fun test() {
        jsonRuleBox.rule = json
        objRuleBox.rule = obj
        valueRuleBox.rule = value
        pairRuleBox.rule = jsonPair
        arrayRuleBox.rule = array

        val r = array.parseOrThrow("[2344, \"some shit\"]", Rules.spaceStr) as List<Any>
        Assert.assertTrue(listOf<Any>(2344, "some shit") == r)
    }
}