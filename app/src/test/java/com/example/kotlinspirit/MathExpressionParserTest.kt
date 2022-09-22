package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.char
import com.example.kotlinspirit.Rules.double
import com.example.kotlinspirit.Rules.space
import org.junit.Assert
import org.junit.Test
import java.lang.IllegalStateException

private val skipper = space.repeat()

val expressionInBrackets = object : Grammar<Double>() {
    override var result: Double = 0.0

    override fun defineRule(): Rule<*> {
        return '(' + expression {
            result = it
        } + ')'
    }

    override fun debug(name: String?): RuleWithDefaultRepeat<Double> {
        return super.debug(name ?: "expressionInBrackets")
    }
}.recursive()

private val expression: Rule<Double> = object : Grammar<Double>() {
    private var sign = '+'
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

    override fun debug(name: String?): RuleWithDefaultRepeat<Double> {
        return super.debug(name ?: "expression")
    }
}.recursive()

class MathExpressionParserTest {
    @Test
    fun test1() {
        val r = expression.debug().compile()
//        Assert.assertEquals(r.parseGetResultOrThrow("5 + 10"), 15.0, 0.0001)
//        Assert.assertEquals(r.parseGetResultOrThrow("5.5 + 10.5"), 16.0, 0.0001)
//        Assert.assertEquals(r.parseGetResultOrThrow("5.5 - 10.5"), -5.0, 0.0001)
//        Assert.assertEquals(r.parseGetResultOrThrow("1.2 / 2.0"), 0.6, 0.0001)
//        Assert.assertEquals(r.parseGetResultOrThrow("1.2 * 2.0"), 2.4, 0.0001)
        Assert.assertEquals(r.parseGetResultOrThrow("(1.2 * 2.0) + (3.4 - 1.2)"), 4.6, 0.0001)
    }
}