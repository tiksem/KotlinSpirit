package com.kotlinspirit

import com.kotlinspirit.core.ParseCode
import com.kotlinspirit.core.ParseException
import com.kotlinspirit.core.ParseResult
import com.kotlinspirit.core.Rules.bigint
import com.kotlinspirit.core.getParseCode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigInteger

class BigIntegerTest {
    @Test
    fun startedWithZero() {
        assertThrows(ParseException::class.java) {
            bigint.compile().parseGetResultOrThrow("034534554")
        }
    }

    @Test
    fun zero() {
        assertEquals(BigInteger.valueOf(0), bigint.compile().parseGetResultOrThrow("0"))
    }

    @Test
    fun minusZero() {
        assertEquals(BigInteger.valueOf(0), bigint.compile().parseGetResultOrThrow("-0"))
    }

    @Test
    fun plusZero() {
        assertEquals(BigInteger.valueOf(0), bigint.compile().parseGetResultOrThrow("+0"))
    }

    @Test
    fun minus() {
        assertEquals(BigInteger.valueOf(-345), bigint.compile().parseGetResultOrThrow("-345"))
    }

    @Test
    fun plus() {
        assertEquals(BigInteger.valueOf(345), bigint.compile().parseGetResultOrThrow("+345"))
    }

    @Test
    fun default() {
        assertEquals(BigInteger.valueOf(23523454), bigint.compile().parseGetResultOrThrow("23523454"))
    }

    @Test
    fun large() {
        assertEquals(
            BigInteger("56237456237845623748526345782364527834652374856234785623434523754627345"),
            bigint.compile().parseGetResultOrThrow(
            "56237456237845623748526345782364527834652374856234785623434523754627345"
        ))
    }

    @Test
    fun largePlus() {
        assertEquals(
            BigInteger("56237456237845623748526345782364527834652374856234785623434523754627345"),
            bigint.compile().parseGetResultOrThrow(
                "+56237456237845623748526345782364527834652374856234785623434523754627345"
            ))
    }

    @Test
    fun largeMinus() {
        assertEquals(
            BigInteger("-56237456237845623748526345782364527834652374856234785623434523754627345"),
            bigint.compile().parseGetResultOrThrow(
                "-56237456237845623748526345782364527834652374856234785623434523754627345"
            ))
    }

    @Test
    fun invalid() {
        val result = ParseResult<BigInteger>()
        bigint.parseWithResult(0, "dsds65537", result)
        assertEquals(ParseCode.INVALID_BIG_INTEGER, result.parseResult.getParseCode())
    }

    @Test
    fun noInt() {
        val r = (!bigint).compile()
        assertEquals(r.matchesAtBeginning("+dsds"), true)
        assertEquals(r.matchesAtBeginning("-dsdsds"), true)
        assertEquals(r.matchesAtBeginning("dsdsds"), true)
        assertEquals(r.matchesAtBeginning("-"), true)
        assertEquals(r.matchesAtBeginning("+"), true)
        assertEquals(r.matchesAtBeginning("+4"), false)
        assertEquals(r.matchesAtBeginning("-0"), false)
        assertEquals(r.matchesAtBeginning("0"), false)
        assertEquals(r.matchesAtBeginning("0345"), true)
        assertEquals(r.matchesAtBeginning("456"), false)
        assertEquals(r.matchesAtBeginning(""), true)
    }
}