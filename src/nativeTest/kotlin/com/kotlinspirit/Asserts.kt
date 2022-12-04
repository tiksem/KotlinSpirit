package com.kotlinspirit

import org.junit.Assert

object Asserts {
    fun listStringEquals(a: List<*>, b: List<*>) {
        assertEquals(a.size, b.size)
        assertEquals(a.joinToString(","), b.joinToString(","))
    }
}