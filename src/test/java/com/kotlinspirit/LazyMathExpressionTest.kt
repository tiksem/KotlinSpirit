package com.kotlinspirit

import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules
import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.double
import org.junit.Assert
import org.junit.Test

private val operator = char('+', '-', '*', '/')
private val value = Rules.lazy { (expressionInBrackets or double) % operator }
private val expressionInBrackets: Rule<*> = value.quoted('(', ')')
private val parser = value.compile()

class LazyMathExpressionTest {
    @Test
    fun test() {
        Assert.assertEquals(parser.tryParse("(1+2)*5"), "(1+2)*5".length)
        Assert.assertEquals(parser.tryParse("1"), "1".length)
        Assert.assertEquals(parser.tryParse("(1/4)+(4/5)*(1/3*(12+7+(5*2)))"), "(1/4)+(4/5)*(1/3*(12+7+(5*2)))".length)
    }
}