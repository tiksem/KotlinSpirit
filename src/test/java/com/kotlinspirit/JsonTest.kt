package com.kotlinspirit

import com.kotlinspirit.core.Rules.jsonArray
import com.kotlinspirit.core.Rules.jsonObject
import com.kotlinspirit.ext.findFirst
import com.kotlinspirit.ext.findLast
import com.kotlinspirit.rangeres.ParseRange
import org.junit.Assert
import org.junit.Test

class JsonTest {
    @Test
    fun testObject() {
        val jsonStringObject = """text { "key": "value", "array": [1, 2, 3], "nested": { "a": true } } some other text """
        Assert.assertEquals(jsonStringObject.findFirst(jsonObject), ParseRange(jsonStringObject.indexOf('{'), jsonStringObject.lastIndexOf('}') + 1))
    }

    @Test
    fun testArray() {
        val jsonStringArray = """text [ { "key": "value" }, { "key2": "value2" } ] some other text """
        Assert.assertEquals(jsonStringArray.findFirst(jsonArray), ParseRange(jsonStringArray.indexOf('['), jsonStringArray.lastIndexOf(']') + 1))
    }

    @Test
    fun reverseTestObject() {
        val jsonStringObject = """text { "key": "value", "array": [1, 2, 3], "nested": { "a": true } } some other text """
        Assert.assertEquals(jsonStringObject.findLast(jsonObject), ParseRange(jsonStringObject.indexOf('{'), jsonStringObject.lastIndexOf('}') + 1))
    }

    @Test
    fun reverseTestArray() {
        val jsonStringArray = """text [ { "key": "value" }, { "key2": "value2" } ] some other text """
        Assert.assertEquals(jsonStringArray.findLast(jsonArray), ParseRange(jsonStringArray.indexOf('['), jsonStringArray.lastIndexOf(']') + 1))
    }
}