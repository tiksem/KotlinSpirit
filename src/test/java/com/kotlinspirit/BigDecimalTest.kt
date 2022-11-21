package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.bigDecimal
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal

private val no = (!bigDecimal).compile()

class BigDecimalTest {
    @Test
    fun testInteger() {
        val str = "23423453453456543435453"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithDot() {
        val str = "23423453453456543435453."
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithDotAndDigits() {
        val str = "23423453453456543435453.2322332"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithE() {
        val str = "23423453453456543435453.2322332e122"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercase() {
        val str = "23423453453456543435453.2322332E122"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithENegative() {
        val str = "23423453453456543435453.2322332e-122"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercaseNegative() {
        val str = "23423453453456543435453.2322332E-122"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEPlus() {
        val str = "23423453453456543435453.2322332e+122"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercasePlus() {
        val str = "23423453453456543435453.2322332E+122"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithENoFraction() {
        val str = "23423453453456543435453.e122"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercaseNoFraction() {
        val str = "23423453453456543435453.E122"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithENegativeNoFraction() {
        val str = "23423453453456543435453.e-122"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercaseNegativeNoFraction() {
        val str = "23423453453456543435453.E-122"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEPlusNoFraction() {
        val str = "23423453453456543435453.e+122"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testDoubleWithEUppercasePlusNoFraction() {
        val str = "23423453453456543435453.E+122"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithE() {
        val str = "23423453453456543435453e5"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithENegative() {
        val str = "23423453453456543435453e-5"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithEUppercase() {
        val str = "23423453453456543435453E5"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerWithENegativeUppercase() {
        val str = "23423453453456543435453E-5"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerNegative() {
        val str = "-23423453453456543435453"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testIntegerPlus() {
        val str = "+23423453453456543435453"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startedWithDot() {
        val str = ".4343343434"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startedWithDotE() {
        val str = ".4343343434e345"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startedWithDotEnegative() {
        val str = ".4343343434e-345"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun notMoreDot() {
        val str = ".4343343.56677"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = ".4343343".length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startsWithDotAndMinus() {
        val str = "-.4343343"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun startsWithDotAndPlus() {
        val str = "+.4343343"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = str.length,
                parseCode = ParseCode.COMPLETE
            )
        )
    }

    @Test
    fun testMinusDotError() {
        val str = "-."
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_BIG_DECIMAL
            )
        )
    }

    @Test
    fun testMinusDotError2() {
        val str = "-.dhfgdhg"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_BIG_DECIMAL
            )
        )
    }

    @Test
    fun testMinusError() {
        val str = "-"
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_BIG_DECIMAL
            )
        )
    }

    private fun testResult(string: String) {
        Assert.assertEquals(
            bigDecimal.compile().parseGetResultOrThrow(string),
            BigDecimal(string)
        )
    }

    @Test
    fun testResults() {
        testResult("23423453453456543435453E-5")
        testResult("23423453453456543435453e-5")
        testResult("-23423453453456543435453e-5")
        testResult("-23423453453456543435453e-54434")
        testResult("35237856237485623478562348756234785623478562347856234785623478562347856234758e-54434")
        testResult("35237856237485623478562348756234785623478562347856234785623478562347856234758")
        testResult("-35237856237485623478562348756234785623478562347856234785623478562347856234758")
        testResult("+35237856237485623478562348756234785623478562347856234785623478562347856234758")
        testResult("+35237856237485623478562348756234785623478562347856234785623478562347856234758E4343")
        testResult("+35237856237485623478562348756234785623478562347856234785623478562347856234758e4343")
    }

    @Test
    fun largeExponent() {
        val str = "+35237856237485623478562348756234785623478562347856234785623478562347856234758e4343323232"
        Assert.assertEquals(ParseCode.INVALID_BIG_DECIMAL, bigDecimal.compile().parse(str).errorCode)
    }

    @Test
    fun testSpaceAfterDouble() {
        val result = ParseResult<BigDecimal>()
        bigDecimal.parseWithResult(0 , "5.0  ", result)
        Assert.assertEquals(result.data ?: BigDecimal(-1.0), BigDecimal("5.0"))
        Assert.assertEquals(result.parseResult.getParseCode(), ParseCode.COMPLETE)
        Assert.assertEquals(result.endSeek, "5.0".length)
    }

    @Test
    fun notMoreDotError() {
        val str = ".."
        Assert.assertEquals(
            bigDecimal.parse(0, str), createStepResult(
                seek = 0,
                parseCode = ParseCode.INVALID_BIG_DECIMAL
            )
        )
    }

    @Test
    fun noParseTest() {
        Assert.assertEquals(no.tryParse("......."), 1)
    }

    @Test
    fun noParseTest3() {
        Assert.assertEquals(no.tryParse("abcdegfrt9.0"), 1)
    }

    @Test
    fun noParseTest4() {
        Assert.assertEquals(no.tryParse("+__fff.0"), 1)
    }

    @Test
    fun noParseTest5() {
        Assert.assertEquals(no.tryParse("-__fff"), 1)
    }

    @Test
    fun noParseTest6() {
        Assert.assertEquals(no.tryParse("+.er4"), 1)
    }

    @Test
    fun noParseTest7() {
        Assert.assertEquals(no.tryParse("-.er4"), 1)
    }
}