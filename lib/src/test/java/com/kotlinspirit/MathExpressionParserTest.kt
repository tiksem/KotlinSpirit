package com.kotlinspirit

import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.double
import com.kotlinspirit.core.Rules.space
import com.kotlinspirit.core.plus
import com.kotlinspirit.grammar.Grammar
import com.kotlinspirit.grammar.nestedResult
import com.kotlinspirit.repeat.RuleWithDefaultRepeat
import org.junit.Assert
import org.junit.Test
import java.lang.IllegalStateException
import java.util.*
import kotlin.math.exp

private val skipper = space.repeat().debug("skipper")

private val expressionInBrackets = nestedResult(
    nested = {
        expression
    },
    entire = {
        '(' + it + ')'
    }
)

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

    override val name: String
        get() = "expression"

}.toRule()

class MathExpressionParserTest {
    @Test
    fun test1() {
        val r = expression.compile()
        Assert.assertEquals(r.parseGetResultOrThrow("5 + 10"), 15.0, 0.0001)
        Assert.assertEquals(r.parseGetResultOrThrow("5.5 + 10.5"), 16.0, 0.0001)
        Assert.assertEquals(r.parseGetResultOrThrow("5.5 - 10.5"), -5.0, 0.0001)
        Assert.assertEquals(r.parseGetResultOrThrow("1.2 / 2.0"), 0.6, 0.0001)
        Assert.assertEquals(r.parseGetResultOrThrow("1.2 * 2.0"), 2.4, 0.0001)
        Assert.assertEquals(r.parseGetResultOrThrow("(1.2 * 2.0) + (3.4 - 1.2)"), 4.6, 0.0001)
        Assert.assertEquals(r.parseGetResultOrThrow("3.4 + ((1.2 * 2.0) + (3.4 - 1.2))"), 8.0, 0.0001)
    }
}