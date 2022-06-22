package com.example.kotlinspirit

import org.junit.Assert
import org.junit.Test

class DoubleTestUsingStep {
    @Test
    fun testInteger() {
        val str = "234239"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testIntegerWithDot() {
        val str = "23423."
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithDotAndDigits() {
        val str = "234.2322332"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithE() {
        val str = "234.23e2"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithEUppercase() {
        val str = "2342.23223E5"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithENegative() {
        val str = "2.23e-2"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithEUppercaseNegative() {
        val str = "2.2E-122"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithEPlus() {
        val str = "2.23e+122"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithEUppercasePlus() {
        val str = "2.2E+12"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithENoFraction() {
        val str = "23.e12"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithEUppercaseNoFraction() {
        val str = "2.E122"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithENegativeNoFraction() {
        val str = "2.e-122"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithEUppercaseNegativeNoFraction() {
        val str = "23.E-100"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithEPlusNoFraction() {
        val str = "23.e+122"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testDoubleWithEUppercasePlusNoFraction() {
        val str = "2.E+12"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testIntegerWithE() {
        val str = "2342e5"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testIntegerWithENegative() {
        val str = "23e-5"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testIntegerWithEUppercase() {
        val str = "2342345345435453E5"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testIntegerWithENegativeUppercase() {
        val str = "235453E-5"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testIntegerNegative() {
        val str = "-2342345435453"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun testIntegerPlus() {
        val str = "+234234"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun startedWithDot() {
        val str = ".4343343434"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun startedWithDotE() {
        val str = ".4343343434e345"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun startedWithDotEnegative() {
        val str = ".4343343434e-345"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(str.toDouble(), result.data)
    }

    @Test
    fun notMoreDot() {
        val str = ".4343343.56677"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = ".4343343".length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals(".4343343".toDouble(), result.data)
    }

    @Test
    fun startsWithDotAndMinus() {
        val str = "-.4343343"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = "-.4343343".length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals("-.4343343".toDouble(), result.data)
    }

    @Test
    fun startsWithDotAndPlus() {
        val str = "+.4343343"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(result.stepResult, createStepResult(
            seek = "+.4343343".length,
            stepCode = StepCode.COMPLETE
        ))
        Assert.assertEquals("+.4343343".toDouble(), result.data)
    }

    @Test
    fun testMinusDotError() {
        val str = "-."
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(
            Rules.double.parse(0, str), createStepResult(
                seek = 0,
                stepCode = StepCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun testMinusDotError2() {
        val str = "-.dhfgdhg"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(
            Rules.double.parse(0, str), createStepResult(
                seek = 0,
                stepCode = StepCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun testMinusError() {
        val str = "-"
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(
            Rules.double.parse(0, str), createStepResult(
                seek = 0,
                stepCode = StepCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun notMoreDotError() {
        val str = ".."
        val result = ParseResult<Double>()
        Rules.double.parseWithResultUsingStep(0, str, result)
        Assert.assertEquals(
            Rules.double.parse(0, str), createStepResult(
                seek = 0,
                stepCode = StepCode.INVALID_DOUBLE
            )
        )
    }
}