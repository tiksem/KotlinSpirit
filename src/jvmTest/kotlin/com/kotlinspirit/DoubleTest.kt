package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.double
import org.junit.Assert
import org.junit.Test

private val noDouble = (!double).compile()

class DoubleTest {
    @Test
    fun testInteger() {
        val str = "23423453453456543435453"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithDot() {
        val str = "23423453453456543435453."
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithDotAndDigits() {
        val str = "23423453453456543435453.2322332"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithE() {
        val str = "23423453453456543435453.2322332e122"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercase() {
        val str = "23423453453456543435453.2322332E122"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithENegative() {
        val str = "23423453453456543435453.2322332e-122"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercaseNegative() {
        val str = "23423453453456543435453.2322332E-122"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEPlus() {
        val str = "23423453453456543435453.2322332e+122"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercasePlus() {
        val str = "23423453453456543435453.2322332E+122"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithENoFraction() {
        val str = "23423453453456543435453.e122"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercaseNoFraction() {
        val str = "23423453453456543435453.E122"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithENegativeNoFraction() {
        val str = "23423453453456543435453.e-122"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercaseNegativeNoFraction() {
        val str = "23423453453456543435453.E-122"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEPlusNoFraction() {
        val str = "23423453453456543435453.e+122"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercasePlusNoFraction() {
        val str = "23423453453456543435453.E+122"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithE() {
        val str = "23423453453456543435453e5"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithENegative() {
        val str = "23423453453456543435453e-5"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithEUppercase() {
        val str = "23423453453456543435453E5"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithENegativeUppercase() {
        val str = "23423453453456543435453E-5"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerNegative() {
        val str = "-23423453453456543435453"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    private fun testFullInfMatch(strings: List<String>, expectedValue: Double) {
        val r = ParseResult<Double>()
        for (str in strings) {
            double.parseWithResult(0, str, r)
            Assert.assertEquals(expectedValue, r.data)
            Assert.assertEquals(r.parseResult.seek, str.length)
        }
    }

    private fun testInfError(strings: List<String>) {
        val r = ParseResult<Double>()
        for (str in strings) {
            double.parseWithResult(0, str, r)
            Assert.assertEquals(r.parseResult.parseCode, ParseCode.INVALID_DOUBLE)
        }
    }

    @Test
    fun testPositiveInf() {
        testFullInfMatch(
            strings = listOf("inf", "+inf", "Inf", "+Inf", "infinity", "+infinity", "Infinity", "+Infinity"),
            expectedValue = Double.POSITIVE_INFINITY
        )
    }

    @Test
    fun testNegativeInf() {
        testFullInfMatch(
            strings = listOf("-inf", "-Inf", "-infinity", "-Infinity"),
            expectedValue = Double.NEGATIVE_INFINITY
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
        val r = ParseResult<Double>()
        val str = "NaN"
        double.parseWithResult(0, str, r)
        Assert.assertEquals("NaN".length, r.parseResult.seek)
        Assert.assertTrue(r.data?.isNaN() == true)
    }

    @Test
    fun testIntegerPlus() {
        val str = "+23423453453456543435453"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startedWithDot() {
        val str = ".4343343434"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startedWithDotE() {
        val str = ".4343343434e345"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startedWithDotEnegative() {
        val str = ".4343343434e-345"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun notMoreDot() {
        val str = ".4343343.56677"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = ".4343343".length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startsWithDotAndMinus() {
        val str = "-.4343343"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startsWithDotAndPlus() {
        val str = "+.4343343"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testMinusDotError() {
        val str = "-."
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = 0,
                parseCode = ParseCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun testMinusDotError2() {
        val str = "-.dhfgdhg"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = 0,
                parseCode = ParseCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun testMinusError() {
        val str = "-"
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
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
        Assert.assertEquals(result.parseResult.parseCode, ParseCode.COMPLETE)
        Assert.assertEquals(result.endSeek, "5.0".length)
    }

    @Test
    fun testPositiveInfinity() {
        val str = "0.0000000000000000000000000000000015e543434334433344443344343434343434343434343434343"
        Assert.assertTrue(
            double.compile().parseGetResultOrThrow(str) == Double.POSITIVE_INFINITY
        )
    }

    @Test
    fun testPositiveInfinity2() {
        val str = "0.0000000000000000000000000000000015E543434334433344443344343434343434343434343434343"
        Assert.assertTrue(
            double.compile().parseGetResultOrThrow(str) == Double.POSITIVE_INFINITY,
        )
    }

    @Test
    fun testNegativeInfinity() {
        val str = "-0.0000000000000000000000000000000015e543434334433344443344343434343434343434343434343"
        Assert.assertTrue(
            double.compile().parseGetResultOrThrow(str) == Double.NEGATIVE_INFINITY
        )
    }

    @Test
    fun testNegativeInfinity2() {
        val str = "0.0000000000000000000000000000000015E-543434334433344443344343434343434343434343434343"
        Assert.assertTrue(
            double.compile().parseGetResultOrThrow(str) == 0.0
        )
    }

    @Test
    fun notMoreDotError() {
        val str = ".."
        Assert.assertEquals(
            double.parse(0, str), ParseSeekResult(
                seek = 0,
                parseCode = ParseCode.INVALID_DOUBLE
            )
        )
    }

    @Test
    fun noParseTest() {
        Assert.assertEquals(noDouble.tryParse("......."), 1)
    }

    @Test
    fun noParseTest3() {
        Assert.assertEquals(noDouble.tryParse("abcdegfrt9.0"), 1)
    }

    @Test
    fun noParseTest4() {
        Assert.assertEquals(noDouble.tryParse("+__fff.0"), 1)
    }

    @Test
    fun noParseTest5() {
        Assert.assertEquals(noDouble.tryParse("-__fff"), 1)
    }

    @Test
    fun noParseTest6() {
        Assert.assertEquals(noDouble.tryParse("+.er4"), 1)
    }

    @Test
    fun noParseTest7() {
        Assert.assertEquals(noDouble.tryParse("-.er4"), 1)
    }
}