package com.kotlinspirit

import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.double
import com.kotlinspirit.core.Rules.grammar
import com.kotlinspirit.core.Rules.space
import com.kotlinspirit.ext.findAll
import org.junit.Assert
import org.junit.Test
import java.lang.IllegalStateException

private val skipper = space.repeat()

private class ExpressionData {
    var sign: Char = 'x'
    var a: Double = 0.0
    var b: Double = 0.0
}

private val expression = grammar(
    dataFactory = { ExpressionData() },
    defineRule = { data ->
        val sign = char('+', '-', '*', '/').invoke {
            data.sign = it
        }

        skipper + (double or expressionInBrackets).invoke {
            data.a = it
        } + skipper + sign + skipper + (double or expressionInBrackets) {
            data.b = it
        } + skipper
    },
    getResult = {
        val a = it.a
        val b = it.b
        when (it.sign) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            else -> throw IllegalStateException("Invalid sign")
        }
    }
)

private val expressionInBrackets: Rule<Double> = expression.quoted('(', ')')

class MathExpressionParserTest {
    @Test
    fun test1() {
        for (r in listOf(expression.compile(debug = true), expression.compile(debug = false))) {
            Assert.assertEquals(r.parseGetResultOrThrow("5 + 10"), 15.0, 0.0001)
            Assert.assertEquals(r.parseGetResultOrThrow("5.5 + 10.5"), 16.0, 0.0001)
            Assert.assertEquals(r.parseGetResultOrThrow("5.5 - 10.5"), -5.0, 0.0001)
            Assert.assertEquals(r.parseGetResultOrThrow("1.2 / 2.0"), 0.6, 0.0001)
            Assert.assertEquals(r.parseGetResultOrThrow("1.2 * 2.0"), 2.4, 0.0001)
            Assert.assertEquals(r.parseGetResultOrThrow("(1.2 * 2.0) + (3.4 - 1.2)"), 4.6, 0.0001)
            Assert.assertEquals(r.parseGetResultOrThrow("3.4 + ((1.2 * 2.0) + (3.4 - 1.2))"), 8.0, 0.0001)

            val values = "5 + 10 (1.2 * 2.0) + (3.4 - 1.2)".findAll(expression.safe())
            Assert.assertEquals(values[0], 15.0, 0.0001)
            Assert.assertEquals(values[1], 4.6, 0.0001)
        }
    }
}