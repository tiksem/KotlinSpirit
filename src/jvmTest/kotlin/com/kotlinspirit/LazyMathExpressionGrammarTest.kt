package com.kotlinspirit

import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.double
import com.kotlinspirit.grammar.Grammar
import org.junit.Assert
import org.junit.Test

private val operator = char('+', '-', '*', '/')

private val value = object : Grammar<Double>() {
    private val numbers = ArrayList<Double>()
    private val operators = StringBuilder()
    override val result: Double
        get() {
            if (numbers.isEmpty()) return 0.0

            // First pass: evaluate * and /, collect intermediate results
            val compressedNumbers = ArrayList<Double>()
            val compressedOperators = ArrayList<Char>()

            var acc = numbers[0]
            for (i in operators.indices) {
                val op = operators[i]
                val next = numbers[i + 1]

                when (op) {
                    '*' -> acc *= next
                    '/' -> acc /= next
                    else -> {
                        // Push current result and save the low-precedence op
                        compressedNumbers.add(acc)
                        compressedOperators.add(op)
                        acc = next
                    }
                }
            }
            compressedNumbers.add(acc) // push the last accumulated number

            // Second pass: evaluate + and -
            var result = compressedNumbers[0]
            for (i in compressedOperators.indices) {
                val op = compressedOperators[i]
                val next = compressedNumbers[i + 1]
                result = when (op) {
                    '+' -> result + next
                    '-' -> result - next
                    else -> throw IllegalArgumentException("Unexpected operator: $op")
                }
            }

            return result
        }

    override fun defineRule(): Rule<*> {
        return (expressionInBrackets or double).invoke {
            numbers.add(it)
        } % operator.invoke {
            operators.append(it)
        }
    }

    override fun resetResult() {
        numbers.clear()
        operators.clear()
    }
}.toRule()

private val expressionInBrackets: Rule<Double> = value.quoted('(', ')')
private val parser = value.compile()

class LazyMathExpressionGrammarTest {
    @Test
    fun test() {
        Assert.assertEquals(parser.tryParse("(1+2)*5"), "(1+2)*5".length)
        Assert.assertEquals(parser.tryParse("1"), "1".length)
        Assert.assertEquals(parser.tryParse("(1/4)+(4/5)*(1/3*(12+7+(5*2)))"), "(1/4)+(4/5)*(1/3*(12+7+(5*2)))".length)

        Assert.assertEquals(parser.parseGetResultOrThrow("(1+2)*5"), 15.0, 0.001)
        Assert.assertEquals(parser.parseGetResultOrThrow("1"), 1.0, 0.001)
        Assert.assertEquals(parser.parseGetResultOrThrow("(1/4)+(4/5)*(1/3*(12+7+(5*2)))"), 7.9833, 0.001)
    }
}