package com.kotlinspirit

import com.kotlinspirit.core.Clearable
import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.double
import com.kotlinspirit.core.Rules.grammar
import org.junit.Assert
import org.junit.Test

private val operator = char('+', '-', '*', '/')

private class ValueData(
    val numbers: ArrayList<Double> = ArrayList(),
    val operators: StringBuilder = StringBuilder()
) : Clearable {
    override fun clear() {
        numbers.clear()
        operators.clear()
    }
}

private val value = grammar(
    dataFactory = { ValueData() },
    defineRule = { data ->
        (expressionInBrackets or double).invoke {
            data.numbers.add(it)
        } % operator.invoke {
            data.operators.append(it)
        }
    },
    getResult = {
        if (it.numbers.isEmpty()) {
            return@grammar 0.0
        }

        // First pass: evaluate * and /, collect intermediate results
        val compressedNumbers = ArrayList<Double>()
        val compressedOperators = ArrayList<Char>()

        var acc = it.numbers[0]
        for (i in it.operators.indices) {
            val op = it.operators[i]
            val next = it.numbers[i + 1]

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

        result
    }
)

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