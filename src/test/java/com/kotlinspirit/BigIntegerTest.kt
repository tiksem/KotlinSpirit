package com.kotlinspirit

import com.kotlinspirit.core.*
import com.kotlinspirit.core.Rules.bigint
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class BigIntegerTest {
    @Test
    fun zero() {
        Assert.assertEquals(BigInteger.valueOf(0), bigint.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun minusZero() {
        Assert.assertEquals(BigInteger.valueOf(0), bigint.compile().parseGetResultOrThrow("-0"))
    }

    @Test
    fun plusZero() {
        Assert.assertEquals(BigInteger.valueOf(0), bigint.compile().parseGetResultOrThrow("+0"))
    }

    @Test
    fun minus() {
        Assert.assertEquals(BigInteger.valueOf(-345), bigint.compile().parseGetResultOrThrow("-345"))
    }

    @Test
    fun plus() {
        Assert.assertEquals(BigInteger.valueOf(345), bigint.compile().parseGetResultOrThrow("+345"))
    }

    @Test
    fun default() {
        Assert.assertEquals(BigInteger.valueOf(23523454), bigint.compile().parseGetResultOrThrow("23523454"))
    }

    @Test
    fun large() {
        Assert.assertEquals(
            BigInteger("56237456237845623748526345782364527834652374856234785623434523754627345"),
            bigint.compile().parseGetResultOrThrow(
            "56237456237845623748526345782364527834652374856234785623434523754627345"
        ))
    }

    @Test
    fun largePlus() {
        Assert.assertEquals(
            BigInteger("56237456237845623748526345782364527834652374856234785623434523754627345"),
            bigint.compile().parseGetResultOrThrow(
                "+56237456237845623748526345782364527834652374856234785623434523754627345"
            ))
    }

    @Test
    fun largeMinus() {
        Assert.assertEquals(
            BigInteger("-56237456237845623748526345782364527834652374856234785623434523754627345"),
            bigint.compile().parseGetResultOrThrow(
                "-56237456237845623748526345782364527834652374856234785623434523754627345"
            ))
    }

    @Test
    fun invalid() {
        val result = ParseResult<BigInteger>()
        bigint.parseWithResult(0, "dsds65537", result)
        Assert.assertEquals(ParseCode.INVALID_BIG_INTEGER, result.parseResult.getParseCode())
    }

    @Test
    fun noInt() {
        val r = (!bigint).compile()
        Assert.assertEquals(r.matchesAtBeginning("+dsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("-dsdsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("dsdsds"), true)
        Assert.assertEquals(r.matchesAtBeginning("-"), true)
        Assert.assertEquals(r.matchesAtBeginning("+"), true)
        Assert.assertEquals(r.matchesAtBeginning("+4"), false)
        Assert.assertEquals(r.matchesAtBeginning("-0"), false)
        Assert.assertEquals(r.matchesAtBeginning("0"), false)
        Assert.assertEquals(r.matchesAtBeginning("0345"), false)
        Assert.assertEquals(r.matchesAtBeginning("456"), false)
        Assert.assertEquals(r.matchesAtBeginning(""), true)
    }
}