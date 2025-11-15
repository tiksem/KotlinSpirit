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

    @Test
    fun testBigJson() {
        val json = """
            {
                "seasonLength": {
                    "question": "Quanto tempo dura a temporada da sua piscina a cada ano?",
                    "type": "radio",
                    "options": [
                        {
                            "text": "Sazonal",
                            "next": "seasonStart"
                        },
                        {
                            "text": "Anual",
                            "next": "marketingOptIn"
                        }
                    ]
                },
                "poolVolume": {
                    "type": "volume",
                    "question": "Qual ? o tamanho da sua piscina, {root}?",
                    "next": "seasonLength"
                },
                "seasonEnd": {
                    "next": "marketingOptIn",
                    "type": "text",
                    "question": "Quando a temporada da sua piscina geralmente termina?",
                    "maxLength": 50,
                    "validation": "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])${'$'}"
                },
                "marketingOptIn": {
                    "nextTrue": "email",
                    "type": "bool",
                    "question": "Voc? gostaria de receber dicas de manuten??o da piscina e atualiza??es de produtos?",
                    "nextFalse": "email"
                },
                "seasonStart": {
                    "next": "seasonEnd",
                    "type": "text",
                    "question": "Quando a temporada da sua piscina geralmente come?a?",
                    "maxLength": 50,
                    "validation": "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])${'$'}"
                },
                "root": {
                    "type": "text",
                    "next": "poolVolume",
                    "validation": "^\\p{L}+([ \\p{Pd}\\']\\p{L}+)*${'$'}",
                    "question": "Qual ? o seu nome?",
                    "maxLength": 30
                },
                "email": {
                    "type": "email",
                    "question": "Obrigado! Precisamos apenas do seu e-mail para enviar os resultados.",
                    "next": null
                },
                "meta": {
                    "shopifyFields": [
                        "poolVolume",
                        "root",
                        "seasonStart",
                        "seasonEnd",
                        "seasonLength"
                    ],
                    "shopifyNameField": "root",
                    "shopifyMarketingField": "marketingOptIn"
                }
            }
        """.trimIndent()

        Assert.assertTrue(jsonObject.compile().matches(json))
    }
}