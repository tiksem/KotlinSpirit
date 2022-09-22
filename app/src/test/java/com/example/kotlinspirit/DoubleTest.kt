package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.double
import org.junit.Assert
import org.junit.Test

class DoubleTest {
    @Test
    fun testInteger() {
        val str = "23423453453456543435453"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithDot() {
        val str = "23423453453456543435453."
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithDotAndDigits() {
        val str = "23423453453456543435453.2322332"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithE() {
        val str = "23423453453456543435453.2322332e122"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercase() {
        val str = "23423453453456543435453.2322332E122"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithENegative() {
        val str = "23423453453456543435453.2322332e-122"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercaseNegative() {
        val str = "23423453453456543435453.2322332E-122"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEPlus() {
        val str = "23423453453456543435453.2322332e+122"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercasePlus() {
        val str = "23423453453456543435453.2322332E+122"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithENoFraction() {
        val str = "23423453453456543435453.e122"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercaseNoFraction() {
        val str = "23423453453456543435453.E122"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithENegativeNoFraction() {
        val str = "23423453453456543435453.e-122"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercaseNegativeNoFraction() {
        val str = "23423453453456543435453.E-122"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEPlusNoFraction() {
        val str = "23423453453456543435453.e+122"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercasePlusNoFraction() {
        val str = "23423453453456543435453.E+122"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithE() {
        val str = "23423453453456543435453e5"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithENegative() {
        val str = "23423453453456543435453e-5"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithEUppercase() {
        val str = "23423453453456543435453E5"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithENegativeUppercase() {
        val str = "23423453453456543435453E-5"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerNegative() {
        val str = "-23423453453456543435453"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerPlus() {
        val str = "+23423453453456543435453"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startedWithDot() {
        val str = ".4343343434"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startedWithDotE() {
        val str = ".4343343434e345"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startedWithDotEnegative() {
        val str = ".4343343434e-345"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun notMoreDot() {
        val str = ".4343343.56677"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = ".4343343".length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startsWithDotAndMinus() {
        val str = "-.4343343"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startsWithDotAndPlus() {
        val str = "+.4343343"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testMinusDotError() {
        val str = "-."
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun testMinusDotError2() {
        val str = "-.dhfgdhg"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun testMinusError() {
        val str = "-"
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun testSpaceAfterDouble() {
        val result = ParseResult<Double>()
        double.parseWithResult(0 , "5.0  ", result)
        Assert.assertEquals(result.data ?: -1.0, 5.0, 0.00001)
        Assert.assertEquals(result.parseResult.getParseCode(), ParseCode.COMPLETE)
        Assert.assertEquals(result.seek, "5.0".length)
    }

    @Test
    fun notMoreDotError() {
        val str = ".."
        Assert.assertEquals(
            double.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun noParseTest() {
        val str = "......."
        Assert.assertEquals(double.noParse(0, str), str.length)
    }

    @Test
    fun noParseTest2() {
        val str = ".......9.0"
        Assert.assertEquals(double.noParse(0, str), str.length - 4)
    }

    @Test
    fun noParseTest3() {
        val str = "abcdegfrt9.0"
        Assert.assertEquals(double.noParse(0, str), str.length - 3)
    }

    @Test
    fun noParseTest4() {
        val str = "+__fff"
        Assert.assertEquals(double.noParse(0, str), str.length)
    }


    @Test
    fun noParseTest5() {
        val str = "-__fff"
        Assert.assertEquals(double.noParse(0, str), str.length)
    }

    @Test
    fun noParseTest6() {
        val str = "+.er4"
        Assert.assertEquals(double.noParse(0, str), str.length - 1)
    }

    @Test
    fun noParseTest7() {
        val str = "-.er4"
        Assert.assertEquals(double.noParse(0, str), str.length - 1)
    }
}