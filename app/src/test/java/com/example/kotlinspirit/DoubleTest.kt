package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.double
import org.junit.Assert
import org.junit.Test

class DoubleTest {
    @Test
    fun testInteger() {
        val str = "23423453453456543435453"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testIntegerWithDot() {
        val str = "23423453453456543435453."
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testDoubleWithDotAndDigits() {
        val str = "23423453453456543435453.2322332"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testDoubleWithE() {
        val str = "23423453453456543435453.2322332e122"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testDoubleWithEUppercase() {
        val str = "23423453453456543435453.2322332E122"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testDoubleWithENegative() {
        val str = "23423453453456543435453.2322332e-122"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testDoubleWithEUppercaseNegative() {
        val str = "23423453453456543435453.2322332E-122"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testDoubleWithENoFraction() {
        val str = "23423453453456543435453.e122"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testDoubleWithEUppercaseNoFraction() {
        val str = "23423453453456543435453.E122"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testDoubleWithENegativeNoFraction() {
        val str = "23423453453456543435453.e-122"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testDoubleWithEUppercaseNegativeNoFraction() {
        val str = "23423453453456543435453.E-122"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testIntegerWithE() {
        val str = "23423453453456543435453e5"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testIntegerWithENegative() {
        val str = "23423453453456543435453e-5"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testIntegerWithEUppercase() {
        val str = "23423453453456543435453E5"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testIntegerWithENegativeUppercase() {
        val str = "23423453453456543435453E-5"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }

    @Test
    fun testIntegerNegative() {
        val str = "-23423453453456543435453"
        Assert.assertEquals(double.parse(0, str), createStepResult(
            seek = str.length,
            stepCode = StepCode.COMPLETE
        ))
    }
}