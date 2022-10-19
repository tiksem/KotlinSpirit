package com.kotlinspirit

import org.junit.Assert

object Asserts {
    fun listStringEquals(a: List<*>, b: List<*>) {
        Assert.assertEquals(a.size, b.size)
        Assert.assertEquals(a.joinToString(","), b.joinToString(","))
    }
}