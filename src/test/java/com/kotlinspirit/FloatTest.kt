package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.float
import junit.framework.AssertionFailedError
import org.junit.Assert
import org.junit.Test

private val noFloat = (!float).compile()

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


    private fun testFullInfMatch(strings: List<String>, expectedValue: Float) {
        val r = ParseResult<Float>()
        for (str in strings) {
            float.parseWithResult(0, str, r)
            if (expectedValue != r.data) {
                throw AssertionFailedError("$str parse failed, $expectedValue != ${r.data}")
            }
            Assert.assertEquals(r.parseResult.getSeek(), str.length)
        }
    }

    private fun testInfError(strings: List<String>) {
        val r = ParseResult<Float>()
        for (str in strings) {
            float.parseWithResult(0, str, r)
            Assert.assertEquals(r.parseResult.getParseCode(), ParseCode.INVALID_FLOAT)
        }
    }

    @Test
    fun testPositiveInf() {
        testFullInfMatch(
            strings = listOf("inf", "+inf", "Inf", "+Inf", "infinity", "+infinity", "Infinity", "+Infinity"),
            expectedValue = Float.POSITIVE_INFINITY
        )
    }

    @Test
    fun testNegativeInf() {
        testFullInfMatch(
            strings = listOf("-inf", "-Inf", "-infinity", "-Infinity"),
            expectedValue = Float.NEGATIVE_INFINITY
        )
    }

    @Test
    fun testInfError() {
        testInfError(
            strings = listOf("in", "In", "i", "ina", "Ina", "-in", "+in", "+in3434")
        )
    }

    @Test
    fun testNan() {
        val r = ParseResult<Float>()
        val str = "NaN"
        float.parseWithResult(0, str, r)
        Assert.assertEquals("NaN".length, r.parseResult.getSeek())
        Assert.assertTrue(r.data?.isNaN() == true)
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
    fun testSpaceAfterFloat() {
        val result = ParseResult<Float>()
        float.parseWithResult(0 , "5.0  ", result)
        Assert.assertEquals(result.data ?: -1.0f, 5.0f, 0.00001f)
        Assert.assertEquals(result.parseResult.getParseCode(), ParseCode.COMPLETE)
        Assert.assertEquals(result.endSeek, "5.0".length)
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
        Assert.assertEquals(noFloat.tryParse("......."), 1)
    }

    @Test
    fun noParseTest3() {
        Assert.assertEquals(noFloat.tryParse("abcdegfrt9.0"), 1)
    }

    @Test
    fun noParseTest4() {
        Assert.assertEquals(noFloat.tryParse("+__fff.0"), 1)
    }

    @Test
    fun noParseTest5() {
        Assert.assertEquals(noFloat.tryParse("-__fff"), 1)
    }

    @Test
    fun noParseTest6() {
        Assert.assertEquals(noFloat.tryParse("+.er4"), 1)
    }

    @Test
    fun noParseTest7() {
        Assert.assertEquals(noFloat.tryParse("-.er4"), 1)
    }
}