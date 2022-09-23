package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rules
import com.kotlinspirit.core.createStepResult
import org.junit.Assert
import org.junit.Test

class DoubleTestResult {
    private fun testDouble(str: String) {
        val result = ParseResult<Double>()
        Rules.double.parseWithResult(0, str, result)
        Assert.assertEquals(result.parseResult,
            createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
        Assert.assertEquals(str.toDouble(), result.data!!, 0.000000001)
    }

    @Test
    fun testInteger() {
        val str = "234234"
        testDouble(str)
    }

    @Test
    fun testIntegerWithDot() {
        val str = "23423."
        testDouble(str)
    }

    @Test
    fun testDoubleWithDotAndDigits() {
        val str = "234.2322332"
        testDouble(str)
    }

    @Test
    fun testDoubleWithE() {
        val str = "234.23e2"
        testDouble(str)
    }

    @Test
    fun testDoubleWithEUppercase() {
        val str = "2342.23223E5"
        testDouble(str)
    }

    @Test
    fun testDoubleWithENegative() {
        val str = "2.23e-2"
        testDouble(str)
    }

    @Test
    fun testDoubleWithEUppercaseNegative() {
        val str = "2.2E-122"
        testDouble(str)
    }

    @Test
    fun testDoubleWithEPlus() {
        val str = "2.23e+122"
        testDouble(str)
    }

    @Test
    fun testDoubleWithEUppercasePlus() {
        val str = "2.2E+12"
        testDouble(str)
    }

    @Test
    fun testDoubleWithENoFraction() {
        val str = "23.e12"
        testDouble(str)
    }

    @Test
    fun testDoubleWithEUppercaseNoFraction() {
        val str = "2.E122"
        testDouble(str)
    }

    @Test
    fun testDoubleWithENegativeNoFraction() {
        val str = "2.e-122"
        testDouble(str)
    }

    @Test
    fun testDoubleWithEUppercaseNegativeNoFraction() {
        val str = "23.E-100"
        testDouble(str)
    }

    @Test
    fun testDoubleWithEPlusNoFraction() {
        val str = "23.e+122"
        testDouble(str)
    }

    @Test
    fun testDoubleWithEUppercasePlusNoFraction() {
        val str = "2.E+12"
        testDouble(str)
    }

    @Test
    fun testIntegerWithE() {
        val str = "2342e5"
        testDouble(str)
    }

    @Test
    fun testIntegerWithENegative() {
        val str = "23e-5"
        testDouble(str)
    }

    @Test
    fun testIntegerWithEUppercase() {
        val str = "2342345345435453E5"
        testDouble(str)
    }

    @Test
    fun testIntegerWithENegativeUppercase() {
        val str = "235453E-5"
        testDouble(str)
    }

    @Test
    fun testIntegerNegative() {
        val str = "-2342345435453"
        testDouble(str)
    }

    @Test
    fun testIntegerPlus() {
        val str = "+234234"
        testDouble(str)
    }

    @Test
    fun startedWithDot() {
        val str = ".4343343434"
        testDouble(str)
    }

    @Test
    fun startedWithDotE() {
        val str = ".4343343434e345"
        testDouble(str)
    }

    @Test
    fun startedWithDotEnegative() {
        val str = ".4343343434e-345"
        testDouble(str)
    }

    @Test
    fun notMoreDot() {
        val str = ".4343343.56677"
        val result = ParseResult<Double>()
        Rules.double.parseWithResult(0, str, result)
        Assert.assertEquals(result.parseResult,
            createStepResult(
                seek = ".4343343".length,
                parseCode = ParseCode.COMPLETE
            )
        )
        Assert.assertEquals(".4343343".toDouble(), result.data!!, 0.000000001)
    }

    @Test
    fun startsWithDotAndMinus() {
        val str = "-.4343343"
        testDouble(str)
    }

    @Test
    fun startsWithDotAndPlus() {
        val str = "+.4343343"
        testDouble(str)
    }

    @Test
    fun testMinusDotError() {
        val str = "-."
        val result = ParseResult<Double>()
        Rules.double.parseWithResult(0, str, result)
        Assert.assertEquals(
            Rules.double.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun testMinusDotError2() {
        val str = "-.dhfgdhg"
        val result = ParseResult<Double>()
        Rules.double.parseWithResult(0, str, result)
        Assert.assertEquals(
            Rules.double.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun testMinusError() {
        val str = "-"
        val result = ParseResult<Double>()
        Rules.double.parseWithResult(0, str, result)
        Assert.assertEquals(
            Rules.double.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun notMoreDotError() {
        val str = ".."
        val result = ParseResult<Double>()
        Rules.double.parseWithResult(0, str, result)
        Assert.assertEquals(
            Rules.double.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_DOUBLE
            )
        )
    }
}