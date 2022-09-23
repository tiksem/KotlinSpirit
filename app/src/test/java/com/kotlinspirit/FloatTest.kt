package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.Rules.float
import com.kotlinspirit.core.createStepResult
import org.junit.Assert
import org.junit.Test

class FloatTest {
    @Test
    fun testInteger() {
        val str = "23423453453456543435453"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithDot() {
        val str = "23423453453456543435453."
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithDotAndDigits() {
        val str = "23423453453456543435453.2322332"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithE() {
        val str = "23423453453456543435453.2322332e122"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithEUppercase() {
        val str = "23423453453456543435453.2322332E122"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithENegative() {
        val str = "23423453453456543435453.2322332e-122"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithEUppercaseNegative() {
        val str = "23423453453456543435453.2322332E-122"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithEPlus() {
        val str = "23423453453456543435453.2322332e+122"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithEUppercasePlus() {
        val str = "23423453453456543435453.2322332E+122"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithENoFraction() {
        val str = "23423453453456543435453.e122"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithEUppercaseNoFraction() {
        val str = "23423453453456543435453.E122"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithENegativeNoFraction() {
        val str = "23423453453456543435453.e-122"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithEUppercaseNegativeNoFraction() {
        val str = "23423453453456543435453.E-122"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithEPlusNoFraction() {
        val str = "23423453453456543435453.e+122"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testFloatWithEUppercasePlusNoFraction() {
        val str = "23423453453456543435453.E+122"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithE() {
        val str = "23423453453456543435453e5"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithENegative() {
        val str = "23423453453456543435453e-5"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithEUppercase() {
        val str = "23423453453456543435453E5"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithENegativeUppercase() {
        val str = "23423453453456543435453E-5"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerNegative() {
        val str = "-23423453453456543435453"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerPlus() {
        val str = "+23423453453456543435453"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startedWithDot() {
        val str = ".4343343434"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startedWithDotE() {
        val str = ".4343343434e345"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startedWithDotEnegative() {
        val str = ".4343343434e-345"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun notMoreDot() {
        val str = ".4343343.56677"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = ".4343343".length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startsWithDotAndMinus() {
        val str = "-.4343343"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startsWithDotAndPlus() {
        val str = "+.4343343"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testMinusDotError() {
        val str = "-."
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_FLOAT
            )
        )
    }

    @Test
    fun testMinusDotError2() {
        val str = "-.dhfgdhg"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_FLOAT
            )
        )
    }

    @Test
    fun testMinusError() {
        val str = "-"
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_FLOAT
            )
        )
    }

    @Test
    fun notMoreDotError() {
        val str = ".."
        Assert.assertEquals(
            float.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_FLOAT
            )
        )
    }

    @Test
    fun noParseTest() {
        val str = "......."
        Assert.assertEquals(float.noParse(0, str), str.length)
    }

    @Test
    fun noParseTest2() {
        val str = ".......9.0"
        Assert.assertEquals(float.noParse(0, str), str.length - 4)
    }

    @Test
    fun noParseTest3() {
        val str = "abcdegfrt9.0"
        Assert.assertEquals(float.noParse(0, str), str.length - 3)
    }

    @Test
    fun noParseTest4() {
        val str = "+__fff"
        Assert.assertEquals(float.noParse(0, str), str.length)
    }


    @Test
    fun noParseTest5() {
        val str = "-__fff"
        Assert.assertEquals(float.noParse(0, str), str.length)
    }

    @Test
    fun noParseTest6() {
        val str = "+.er4"
        Assert.assertEquals(float.noParse(0, str), str.length - 1)
    }

    @Test
    fun noParseTest7() {
        val str = "-.er4"
        Assert.assertEquals(float.noParse(0, str), str.length - 1)
    }
}