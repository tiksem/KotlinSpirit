package com.kotlinspirit

import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.double
import com.kotlinspirit.core.Rules.space
import com.kotlinspirit.ext.findAll
import com.kotlinspirit.grammar.Grammar
import org.junit.Assert
import org.junit.Test
import java.lang.IllegalStateException

private val skipper = space.repeat()

private val expression: Rule<Double> = object : Grammar<Double>() {
    private var sign = 'x'
    private var a = 0.0
    private var b = 0.0

    override val result: Double
        get() {
            return when (sign) {
                '+' -> a + b
                '-' -> a - b
                '*' -> a * b
                '/' -> a / b
                else -> throw IllegalStateException("Invalid sign")
            }
        }

    override fun defineRule(): Rule<*> {
        val sign = char('+', '-', '*', '/').invoke {
            this.sign = it
        }

        return skipper + (double or expressionInBrackets) {
            a = it
        } + skipper + sign + skipper + (double or expressionInBrackets) {
            b = it
        } + skipper
    }

    override fun resetResult() {
        super.resetResult()
        a = 0.0
        b = 0.0
        sign = 'x'
    }
}.toRule()

private val expressionInBrackets = expression.quoted('(', ')')

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